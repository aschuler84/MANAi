package at.mana.idea.editor;

import at.mana.idea.configuration.ManaRaplConfigurationUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.testFramework.TestFrameworkUtil;
import com.intellij.testIntegration.TestFramework;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ManaEditorNotifcationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {

    private static final Key<String> KEY = new Key<>("ManaEditorNotification");

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @Nullable EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile( file );
        if( psiFile instanceof PsiJavaFile) {
            PsiJavaFile clazz = (PsiJavaFile) psiFile;
            if( clazz.getName().contains( "Test" ) ) {

                if( ManaRaplConfigurationUtil.findExecutablePath( "RAAPL_HOME", "execute_rapl_idea" ) == null ) {
                    EditorNotificationPanel banner = new EditorNotificationPanel(new JBColor(new Color(237, 180, 180), new Color(237, 180, 180)));
                    banner.text("Please specify the RAPL_HOME environment variable");
                    banner.createActionLabel("Configure environment variable", () -> {
                        ShowSettingsUtil.getInstance().showSettingsDialog(project, "Path Variables");
                    });
                    return banner;
                }
            }
        }
        return null;
    }
}
