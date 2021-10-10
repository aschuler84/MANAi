package at.mana.idea.configuration;

import at.mana.idea.service.ManaProjectService;
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
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.util.EnvironmentUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Date;

public class ManaRaplRunProfileState extends CommandLineState {
    private static final String M2_HOME_KEY = "M2_HOME";
    private static final String RAPL_HOME_KEY = "RAPL_HOME";
    private static final String MVN_FAIL_MESSAGE = "Could not execute Mana RAPL profiler, unable to find maven installation. Did you properly set M2_HOME environment variable?";
    private static final String RAPL_FAIL_MESSAGE = "Could not execute Mana RAPL profiler, unable to find RAPL installation. Did you properly set RAPL_HOME environment variable?";
    private static final String MVN_COMMAND_PHASE = "clean test";

    protected ManaRaplRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected @NotNull ProcessHandler startProcess() throws ExecutionException {
        ManaProjectService service = ServiceManager.getService(this.getEnvironment().getProject(),  ManaProjectService.class);
        ManaRaplConfiguration configuration = (ManaRaplConfiguration) this.getEnvironment().getRunProfile();
        final var raplPath = configuration.findRaplExecutablePath( RAPL_HOME_KEY);
        if( raplPath == null ) throw new ExecutionException( RAPL_FAIL_MESSAGE );
        final var mavenHome = configuration.findMavenHome( M2_HOME_KEY );
        if( mavenHome == null ) throw new ExecutionException(MVN_FAIL_MESSAGE);


        GeneralCommandLine commandLine = new GeneralCommandLine(ManaRaplConfigurationUtil.RAPL_EXECUTABLE_NAME)
                .withExePath( raplPath )
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
        return processHandler;
    }






}
