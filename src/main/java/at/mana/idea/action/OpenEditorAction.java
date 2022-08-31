package at.mana.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.testFramework.LightVirtualFile;
import org.jetbrains.annotations.NotNull;


public class OpenEditorAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //VirtualFileManager.getInstance().
        FileEditorManager.getInstance(e.getProject()).openEditor(
                new OpenFileDescriptor(e.getProject(), new LightVirtualFile("dummy.mana")),true);
    }
}
