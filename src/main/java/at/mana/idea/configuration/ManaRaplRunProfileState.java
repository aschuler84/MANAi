package at.mana.idea.configuration;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.util.EnvironmentUtil;
import org.jetbrains.annotations.NotNull;

public class ManaRaplRunProfileState extends CommandLineState {

    protected ManaRaplRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        // TODO: implement a command, that executes a particular test over and over again.
        GeneralCommandLine commandLine = new GeneralCommandLine("mvn.cmd");
        //System.out.println(PathEnvironmentVariableUtil.getPathVariableValue());
        // Ensure that environment variable RAPL.HOME is set and use its value to start RAPL
        String mavenHome = EnvironmentUtil.getValue("M2_HOME");
        commandLine.withEnvironment("M2_HOME", mavenHome);

        commandLine.addParameter( "-v" );
        commandLine.setWorkDirectory( this.getEnvironment().getProject().getBasePath() );
        ProcessHandlerFactory factory = ProcessHandlerFactory.getInstance();
        OSProcessHandler processHandler = factory.createProcessHandler(commandLine);
        ProcessTerminatedListener.attach(processHandler);
        return processHandler;
    }
}
