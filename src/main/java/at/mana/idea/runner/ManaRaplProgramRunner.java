package at.mana.idea.runner;

import at.mana.idea.configuration.ManaRaplConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.process.ScriptRunnerUtil;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;

import java.util.Objects;

public class ManaRaplProgramRunner extends GenericProgramRunner<ManaRaplProgramRunnerSettings> {
    public @NotNull
    @NonNls String getRunnerId() {
        return "manaRaplRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return profile instanceof ManaRaplConfiguration;
    }

    /*@Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        //Notification notification = new Notification("ManaNotificationGroup", AllIcons.Debugger.ThreadStates.Socket, NotificationType.INFORMATION);
        //notification.setContent( "Mann RAPL Executor started" );
        //Notifications.Bus.notify(notification);
        Objects.requireNonNull( environment.getState() );
        ExecutionResult result = environment.getState().execute( environment.getExecutor(),this );
        new RunContentBuilder(result, environment).showRunContent(environment.getContentToReuse());
        //System.out.println( result );
        //ToolWindow toolWindow = ToolWindowManager.getInstance(environment.getProject()).getToolWindow("Run");
        //toolWindow.getContentManager().getFactory().createContent(result.getExecutionConsole().getComponent(), "Console", true);
    }*/

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        ExecutionResult result = environment.getState().execute( environment.getExecutor(),this );
        return new RunContentBuilder(result, environment).showRunContent(environment.getContentToReuse());
    }
}
