/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import at.mana.idea.service.DataAcquisitionService;
import at.mana.idea.service.ManaService;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.target.LanguageRuntimeType;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ManaRaplJarConfiguration extends ApplicationConfiguration {

    private String outputFolder;
    private String jarPath;

    protected ManaRaplJarConfiguration(String name, @NotNull Project project, @NotNull ConfigurationFactory factory) {
        super(name, project, factory);
        initJarPath();
    }

    private void initJarPath() {
        // TODO: resolve path relative to plugin home directory
        jarPath = "/Users/andreasschuler/.m2/repository/at/mana/cli/1.0-SNAPSHOT/cli-1.0-SNAPSHOT-exec.jar";
    }


    @Override
    protected @NotNull ManaRaplConfigurationOptions getOptions() {
        return (ManaRaplConfigurationOptions) super.getOptions();
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

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getOutputFolder() {
        if( this.outputFolder == null ) {
            this.outputFolder = getProject().getBasePath() + File.separator + ".mana";
        }
        return this.outputFolder;
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new ManaRaplConfigurationEditor();
    }


    @Override
    public boolean isBuildProjectOnEmptyModuleList() {
        return false;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        /*JavaParametersUtil.checkAlternativeJRE(this);
        final String className = getMainClassName();
        if (className == null || className.length() == 0) {
            throw new RuntimeConfigurationError(ExecutionBundle.message("no.main.class.specified.error.text"));
        }
        if (getScratchFileUrl() == null) {
            throw new RuntimeConfigurationError(JavaCompilerBundle.message("error.no.scratch.file.associated.with.configuration"));
        }
        if (getScratchVirtualFile() == null) {
            throw new RuntimeConfigurationError(JavaCompilerBundle.message("error.associated.scratch.file.not.found"));
        }
        ProgramParametersUtil.checkWorkingDirectoryExist(this, getProject(), getConfigurationModule().getModule());
        JavaRunConfigurationExtensionManager.checkConfigurationIsValid(this); */
    }

    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment env) throws ExecutionException {
        final JavaCommandLineState state = new JavaApplicationCommandLineState<>(this, env) {

            @Override
            protected JavaParameters createJavaParameters() throws ExecutionException {
                final JavaParameters params = new JavaParameters();
                final String jreHome = myConfiguration.isAlternativeJrePathEnabled() ? myConfiguration.getAlternativeJrePath() : null;
                // TODO: make this configurable
                setProgramParameters( "rapl -p 9999 -i /Users/andreasschuler/Documents/repository/instrument-mana-test -n 5" );
                params.setJdk(JavaParametersUtil.createProjectJdk(myConfiguration.getProject(), jreHome));
                setupJavaParameters(params);
                params.setJarPath(FileUtil.toSystemDependentName(myConfiguration.jarPath));
                return params;
            }

            @NotNull
            @Override
            protected OSProcessHandler startProcess() throws ExecutionException {
                final OSProcessHandler handler = super.startProcess();
                DataAcquisitionService service = DataAcquisitionService.getInstance( this.getEnvironment().getProject() );
                handler.addProcessListener( service );
                service.startDataAcquisition( this.getEnvironment().getProject() );
                return handler;
            }
        };
        state.setConsoleBuilder(TextConsoleBuilderFactory.getInstance().createBuilder(getProject(), getConfigurationModule().getSearchScope()));

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
}
