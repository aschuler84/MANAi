/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import at.mana.core.util.OperatingSystemUtil;
import at.mana.idea.settings.ManaSettingsState;
import at.mana.idea.util.I18nUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.EnvironmentUtil;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaRaplConfigurationUtil {

    public static final String RAPL_EXECUTABLE_NAME = "execute_rapl_idea";
    public static final String MAVEN_EXECUTABLE_NAME = "mvn";
    public static final String M2_HOME_KEY = "M2_HOME";
    public static final String RAPL_HOME_KEY = "RAPL_HOME";

    public static String getMvnCommand( ) {
        return OperatingSystemUtil.getOperatingSystemType() == OperatingSystemUtil.OperatingSystemType.Windows
                ? "mvn.cmd" : "mvn";
    }

    public static String findManaPluginLibPath(   ) {
        var plugin = PluginManager
                .getInstance().findEnabledPlugin(
                        PluginId.getId( I18nUtil.LITERALS.getString( I18nUtil.PLUGIN_ID ) ) );
        if( plugin != null ) {
            var libPath = new File(plugin.getPluginPath().toAbsolutePath() + File.separator + "lib");
            return libPath.exists() ? libPath.getAbsolutePath() : null;
        }
        return null;
    }

    public static String findManaCliExecutable() {
        return ManaRaplConfigurationUtil.findManaPluginLibPath() + File.separator + I18nUtil.LITERALS.getString( I18nUtil.MANA_CLI );
    }

    public static String findMavenHome() {
        return EnvironmentUtil.getValue(M2_HOME_KEY);
    }

    public static String findExecutablePath(String key, String executableName ) {
        var raplHome = EnvironmentUtil.getValue(key);
        if( raplHome == null ) {
            File raplExec = PathEnvironmentVariableUtil.findInPath( executableName );
            if( raplExec != null && raplExec.exists() ) {
                return raplExec.getAbsolutePath();
            }
        } else {
            return raplHome;
        }
        return null;
    }

    public static boolean verifyMavenManaPluginAvailable( Project project ) {  //Requires read action
        return ReadAction.compute( () -> {
            PsiFile[] files = FilenameIndex.getFilesByName(project, "pom.xml", GlobalSearchScope.projectScope(project));
            if (files.length == 0) {
                return false;
            } else {
                for (var file : files) {
                    if (file instanceof XmlFile) {
                        var xmlFile = (XmlFile) file;
                        var rootElement = xmlFile.getRootTag();

                        return !XmlUtil.processXmlElements(rootElement, element -> {
                            if (element instanceof XmlTag) {
                                var xmlElement = (XmlTag) element;
                                if (xmlElement.getName().equals("plugin")) {
                                    var groupId = xmlElement.getSubTagText("groupId");
                                    var artifactId = xmlElement.getSubTagText("artifactId");
                                    return !("at.mana".equals(groupId) && "instrument-maven-plugin".equals(artifactId));
                                }
                            }
                            return true;
                        }, true);

                    }
                }
            }
            return false;
        });
    }

    public static boolean verifyMavenManaPluginAvailable( Project project, XmlFile pomFile ) {
        var rootElement = pomFile.getRootTag();
        return !XmlUtil.processXmlElements( rootElement, element -> {
            if( element instanceof XmlTag) {
                var xmlElement = (XmlTag) element;
                if( xmlElement.getName().equals( "plugin" ) ) {
                    var groupId = xmlElement.getSubTagText( "groupId" );
                    var artifactId = xmlElement.getSubTagText( "artifactId" );
                    return !("at.mana".equals( groupId ) && "instrument-maven-plugin".equals(artifactId ));
                }
            }
            return true;
        }, true );
    }

    public static boolean isPortAvailable(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    public static void verifyManaInstrumentPluginAvailable( final Project project, final ProcessListener listener ) {
        var artifactName = ManaSettingsState.getInstance().manaInstrumentPlugin;
        var task = new Task.Backgroundable( project, I18nUtil.LITERALS.getString("configuration.mana.instrument.title.verify") ){

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    GeneralCommandLine commandLine = new GeneralCommandLine( getMvnCommand() );
                            //.withExePath( findExecutablePath("", "mvn") + File.separator + MAVEN_EXECUTABLE_NAME);
                    commandLine.addParameter("dependency:get");  // mvn command parameter
                    commandLine.addParameter("-Dartifact=" + artifactName);
                    commandLine.setWorkDirectory(project.getBasePath());
                    ProcessHandlerFactory factory = ProcessHandlerFactory.getInstance();
                    OSProcessHandler processHandler = factory.createColoredProcessHandler(commandLine);
                    ProcessTerminatedListener.attach(processHandler);
                    processHandler.addProcessListener(listener);
                    processHandler.startNotify();
                    processHandler.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // listener gets informed by status code - status code determines if plugin is available
            }

        };
        var indicator = new BackgroundableProcessIndicator( project, task );
        ProgressManager.getInstance().runProcessWithProgressAsynchronously( task, indicator );
    }

    public static void installManaInstrumentPluginAvailable( final Project project, final ProcessListener listener ) {
        var artifactPath= findManaPluginLibPath() + File.separator +
                ManaSettingsState.getInstance().manaInstrumentPlugin;
        var task = new Task.Backgroundable( project, I18nUtil.LITERALS.getString("configuration.mana.instrument.title.install") ){
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    GeneralCommandLine commandLine = new GeneralCommandLine( getMvnCommand() );
                            //.withExePath( findExecutablePath("", "mvn") );
                    commandLine.addParameter("org.apache.maven.plugins:maven-install-plugin:3.0.0-M1:install-file");  // mvn command parameter
                    commandLine.addParameter("-Dfile=" + artifactPath);
                    commandLine.setWorkDirectory(project.getBasePath());
                    ProcessHandlerFactory factory = ProcessHandlerFactory.getInstance();
                    OSProcessHandler processHandler = factory.createColoredProcessHandler(commandLine);
                    ProcessTerminatedListener.attach(processHandler);
                    processHandler.addProcessListener(listener);
                    processHandler.startNotify();
                    processHandler.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        var indicator = new BackgroundableProcessIndicator( project, task );
        ProgressManager.getInstance().runProcessWithProgressAsynchronously( task, indicator );
    }

}
