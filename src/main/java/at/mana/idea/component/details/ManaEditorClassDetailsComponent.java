package at.mana.idea.component.details;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

public class ManaEditorClassDetailsComponent extends ManaEditorDetailsComponent {

    public ManaEditorClassDetailsComponent(VirtualFile file, String title, String description) {
        super(file, title, description);
    }

    @Override
    protected JComponent createContent() {
        return new JPanel();
    }
}
