package at.mana.idea.component.details;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MethodEditorNamedConfigurable extends ManaEditorNamedConfigurable{

    private ManaEditorMethodDetailsComponent methodDetails;
    private JComponent component;

    public MethodEditorNamedConfigurable(VirtualFile file, String displayName, String description) {
        super(file, displayName, description);
    }

    @Override
    protected void init() {
        this.methodDetails = new ManaEditorMethodDetailsComponent( this.file, this.displayName, this.description );
        this.component  = this.methodDetails.createComponent();
    }

    @Override
    public JComponent createOptionsPanel() {
        return this.component;
    }

    @Override
    public @Nullable Icon getIcon(boolean expanded) {
        return AllIcons.Nodes.Method;
    }
}
