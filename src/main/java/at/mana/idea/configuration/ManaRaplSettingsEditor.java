package at.mana.idea.configuration;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ManaRaplSettingsEditor extends SettingsEditor<ManaRaplConfiguration> {

    private JPanel contentPanel = new JPanel();

    @Override
    protected void resetEditorFrom(@NotNull ManaRaplConfiguration s) {

    }

    @Override
    protected void applyEditorTo(@NotNull ManaRaplConfiguration s) throws ConfigurationException {

    }

    @Override
    protected @NotNull JComponent createEditor() {
        contentPanel.add( new JLabel("First Configuration") );
        return contentPanel;
    }
}
