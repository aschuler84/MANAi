package at.mana.idea.component.details;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ClassEditorNamedConfigurable extends ManaEditorNamedConfigurable {

    private ManaEditorClassDetailsComponent classDetails;
    private JComponent detailsComponent;

    public ClassEditorNamedConfigurable(VirtualFile file, String displayName, String description) {
        super(file, displayName, description);
    }

    @Override
    protected void init() {
        this.classDetails = new ManaEditorClassDetailsComponent( this.file, this.displayName, this.description );
        this.detailsComponent = classDetails.createComponent();
    }

    @Override
    public JComponent createOptionsPanel() {
        return this.detailsComponent;
    }

    @Override
    public @Nullable Icon getIcon(boolean expanded) {
        return AllIcons.Nodes.Class;
    }
}
