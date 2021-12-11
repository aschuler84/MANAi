/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;

public class ManaRaplRunProfileState extends CommandLineState {

    private static final String MVN_FAIL_MESSAGE = "Could not execute Mana RAPL profiler, unable to find maven installation. Did you properly set M2_HOME environment variable?";
    private static final String RAPL_FAIL_MESSAGE = "Could not execute Mana RAPL profiler, unable to find RAPL installation. Did you properly set RAPL_HOME environment variable?";
    private static final String MVN_COMMAND_PHASE = "clean test";

    protected ManaRaplRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        /*ManaProjectService service = this.getEnvironment().getProject().getService( ManaProjectService.class );
        ManaRaplJarConfiguration configuration = (ManaRaplJarConfiguration) this.getEnvironment().getRunProfile();
        final var raplPath = configuration.findRaplExecutablePath( RAPL_HOME_KEY);
        if( raplPath == null ) throw new ExecutionException( RAPL_FAIL_MESSAGE );
        final var mavenHome = configuration.findMavenHome( M2_HOME_KEY );
        if( mavenHome == null ) throw new ExecutionException(MVN_FAIL_MESSAGE);


        GeneralCommandLine commandLine = new GeneralCommandLine(ManaRaplConfigurationUtil.RAPL_EXECUTABLE_NAME)
                .withExePath( raplPath + File.separator +  ManaRaplConfigurationUtil.RAPL_EXECUTABLE_NAME )
                .withEnvironment( M2_HOME_KEY, mavenHome )
                .withEnvironment( RAPL_HOME_KEY, raplPath );
        //GeneralCommandLine commandLine = new GeneralCommandLine("/Users/andreasschuler/Dropbox/Dokumente/Dissertation/repository/testsuiteoptimizer/exec/src/main/resources/rapl/execute_rapl_idea");
        commandLine.addParameter( configuration.getOutputFolder() + File.separator + ManaProjectServiceImpl.FOLDER_DATE.format(new Date()));
        commandLine.addParameter( configuration.getNoOfSamples() + "" );
        //commandLine.addParameter( "-DsamplingRate=" + configuration.getSamplingRate());
        //commandLine.addParameter( "-DnoOfSamples=" + configuration.getNoOfSamples());
        commandLine.addParameter( MVN_COMMAND_PHASE );  // mvn command parameter
        commandLine.setWorkDirectory( this.getEnvironment().getProject().getBasePath() );
        ProcessHandlerFactory factory = ProcessHandlerFactory.getInstance();
        OSProcessHandler processHandler = factory.createColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler);
        processHandler.addProcessListener( service );
        return processHandler;*/
        return null;
    }






}
