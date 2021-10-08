package at.mana.idea;

import at.mana.idea.service.ManaProjectService;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;


public class ManaPluginStartup implements StartupActivity {

    private static final Logger logger = Logger.getInstance( ManaPluginStartup.class );


    @Override
    public void runActivity(@NotNull Project project) {
        ManaProjectService service = ServiceManager.getService(project,  ManaProjectService.class);
        service.init();
        MessageBusConnection connection = project.getMessageBus().connect();
        // the project service should be informed whenever files are changed
        connection.subscribe(VirtualFileManager.VFS_CHANGES, service );
        // initially build model from all mana files
    }

}
