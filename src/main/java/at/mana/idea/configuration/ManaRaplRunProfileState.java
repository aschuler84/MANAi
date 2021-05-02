package at.mana.idea.configuration;

import at.mana.idea.service.impl.ManaProjectServiceImpl;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.util.EnvironmentUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Date;

public class ManaRaplRunProfileState extends CommandLineState {

    protected ManaRaplRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }



    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine("/Users/andreasschuler/Dropbox/Dokumente/Dissertation/repository/testsuiteoptimizer/exec/src/main/resources/rapl/execute_rapl_idea");
        ManaRaplConfiguration configuration = (ManaRaplConfiguration) this.getEnvironment().getRunProfile();
        // System.out.println(PathEnvironmentVariableUtil.getPathVariableValue());
        // Ensure that environment variable RAPL.HOME is set and use its value to start RAPL
        String mavenHome = EnvironmentUtil.getValue("M2_HOME");
        if( mavenHome == null ) {
            File mavenExec = PathEnvironmentVariableUtil.findInPath( "mvn" );
            if( mavenExec == null || !mavenExec.exists() ) {
                throw new ExecutionException( "Could not execute Mana RAPL profiler, unable to find maven installation. Did you properly set M2_HOME environment variable?" );
            } else {
                commandLine.withEnvironment("M2_HOME", PathEnvironmentVariableUtil.findInPath("mvn").getAbsolutePath());
            }
        } else {
            commandLine.withEnvironment("M2_HOME", mavenHome);
        }
        //commandLine.withEnvironment("RAPL_HOME","/Users/andreasschuler/Dropbox/Dokumente/Dissertation/repository/testsuiteoptimizer/exec/src/main/resources/rapl");
        commandLine.addParameter( configuration.getOutputFolder() + File.separator + ManaProjectServiceImpl.FOLDER_DATE.format(new Date()));
        commandLine.addParameter( configuration.getNoOfSamples() + "" );
        //commandLine.addParameter( "-DsamplingRate=" + configuration.getSamplingRate());
        //commandLine.addParameter( "-DnoOfSamples=" + configuration.getNoOfSamples());
        commandLine.addParameter( "test" );
        commandLine.setWorkDirectory( this.getEnvironment().getProject().getBasePath() );
        ProcessHandlerFactory factory = ProcessHandlerFactory.getInstance();
        OSProcessHandler processHandler = factory.createColoredProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler);
        return processHandler;
    }
}
