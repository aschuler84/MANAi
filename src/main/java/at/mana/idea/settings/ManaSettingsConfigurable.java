package at.mana.idea.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ManaSettingsConfigurable  implements Configurable {

    private ManaSettingsComponent settingsComponent;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "MANAi";
    }

    @Override
    public @Nullable JComponent createComponent() {
        this.settingsComponent = new ManaSettingsComponent( );
        return this.settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        ManaSettingsState settings = ManaSettingsState.getInstance();
        //boolean modified = !mySettingsComponent.getUserNameText().equals(settings.userId);
        //modified |= mySettingsComponent.getIdeaUserStatus() != settings.ideaStatus;
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        ManaSettingsState settings = ManaSettingsState.getInstance();
    }

    @Override
    public void reset() {
        Configurable.super.reset();
    }

    @Override
    public void disposeUIResources() {
        this.settingsComponent = null;
    }
}
