package at.mana.idea.component.details;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

public class ManaEditorMethodDetailsComponent extends ManaEditorDetailsComponent {


    public ManaEditorMethodDetailsComponent(VirtualFile file, String title, String description) {
        super(file, title, description);
    }

    @Override
    protected JComponent createContent() {
        return new JPanel();
    }


}
