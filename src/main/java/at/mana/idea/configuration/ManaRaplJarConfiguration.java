/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import at.mana.idea.ManaPluginStartup;
import at.mana.idea.service.DataAcquisitionService;
import at.mana.idea.util.I18nUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.LanguageRuntimeType;
import com.intellij.execution.target.TargetEnvironmentConfiguration;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

import static at.mana.idea.configuration.ManaRaplConfigurationUtil.*;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaRaplJarConfiguration extends ApplicationConfiguration {

    private String jarPath;
    private PsiClass selectedClass;

    protected ManaRaplJarConfiguration(String name, @NotNull Project project, @NotNull ConfigurationFactory factory) {
        super(name, project, factory);
        initJarPath();
    }

    private void initJarPath() {
        jarPath = ManaRaplConfigurationUtil.findManaCliExecutable();
    }

    @Override
    protected @NotNull ManaRaplConfigurationOptions getOptions() {
        return (ManaRaplConfigurationOptions) super.getOptions();
    }

    private String getJarPath() {
        if( this.jarPath == null )
            this.initJarPath();
        return this.jarPath;
    }

    public int getNoOfSamples() {
        return getOptions().getNoOfSamples();
    }

    public void setNoOfSamples(int noOfSamples) {
        getOptions().setNoOfSamples(noOfSamples);
    }

    public int getSamplingRate() {
        return getOptions().getSamplingRate();
    }

    public void setSamplingRate(int samplingRate) {
        getOptions().setSamplingRate( samplingRate );
    }

    public int getConnectionPort() {
        return getOptions().getConnectionPort();
    }

    public void setConnectionPort(int connectionPort) {
        getOptions().setConnectionPort( connectionPort );
    }

    public String getSelectedClass() {
        return getOptions().getSelectedClass();
    }

    public PsiClass getSelectedPsiClass() {
        String className = getSelectedClass();
        if( className != null )
           return JavaPsiFacade.getInstance(this.getProject()).findClass(className, GlobalSearchScope.projectScope(getProject()));
        return null;
    }

    public void setSelectedClass( String selectedClass ){
        getOptions().setSelectedClass( selectedClass );
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new ManaRaplConfigurationEditor( this.getProject() );
    }

    @Override
    public boolean isBuildProjectOnEmptyModuleList() {
        return false;
    }

    public String findMavenHome( String key ) {
        return ManaRaplConfigurationUtil.findExecutablePath( key, "mvn" );
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if( ManaRaplConfigurationUtil.findManaCliPath() == null )
            throw new RuntimeConfigurationException(I18nUtil.LITERALS.getString( "configuration.cli.exception" ));

        if( findMavenHome( ManaRaplConfigurationUtil.M2_HOME_KEY) == null )
            throw new RuntimeConfigurationException(I18nUtil.LITERALS.getString("configuration.maven.exception"));

        if (!verifyMavenManaPluginAvailable(this.getProject())) {
            throw new RuntimeConfigurationException(I18nUtil.LITERALS.getString("configuration.mana.exception"));
        }

        if( !isPortAvailable( this.getConnectionPort() ) ) {
            throw  new RuntimeConfigurationException(
                    String.format( I18nUtil.LITERALS.getString("configuration.port.exception") ,getConnectionPort()) );
        }

    }

    private String buildManaCommandLine( ManaRaplJarConfiguration configuration ) {
        //command: rapl -p 9999 -i /Users/andreasschuler/Documents/repository/instrument-mana-test -n 5
        return String.format( "rapl -p %d -i %s -n %d",
                configuration.getConnectionPort(),
                getProject().getBasePath(),
                configuration.getNoOfSamples() );
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        final JavaCommandLineState state = new JavaApplicationCommandLineState<>(this, env) {

            @Override
            protected JavaParameters createJavaParameters() throws ExecutionException {
                final JavaParameters params = new JavaParameters();
                final String jreHome = myConfiguration.isAlternativeJrePathEnabled() ? myConfiguration.getAlternativeJrePath() : null;
                setProgramParameters( buildManaCommandLine( myConfiguration ) );
                params.setJdk(JavaParametersUtil.createProjectJdk(myConfiguration.getProject(), jreHome));
                setupJavaParameters(params);
                params.setJarPath(FileUtil.toSystemDependentName(myConfiguration.getJarPath()));
                return params;
            }

            @NotNull
            @Override
            protected OSProcessHandler startProcess() throws ExecutionException {

                if (!ManaRaplConfigurationUtil.verifyMavenManaPluginAvailable(this.getEnvironment().getProject())) {
                    throw new ExecutionException(I18nUtil.LITERALS.getString("configuration.mana.exception"));
                }

                final OSProcessHandler handler = super.startProcess();
                DataAcquisitionService service =
                        DataAcquisitionService.getInstance( this.getEnvironment().getProject() );
                handler.addProcessListener( service );
                service.startDataAcquisition( this.getEnvironment().getProject() );
                return handler;
            }
        };
        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance()
                .createBuilder(getProject(), getConfigurationModule().getSearchScope()));
        return state;
    }

    @Nullable
    @Override
    public LanguageRuntimeType<?> getDefaultLanguageRuntimeType() {
        return null;
    }

    @Nullable
    @Override
    public String getDefaultTargetName() {
        return null;
    }

    @Override
    public boolean canRunOn(@NotNull TargetEnvironmentConfiguration target) {
        return super.canRunOn(target) && ManaRaplConfigurationUtil.verifyMavenManaPluginAvailable( this.getProject() );
    }
}
