package at.mana.idea.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ManaRaplConfigurationType implements ConfigurationType {

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "Mana";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) String getConfigurationTypeDescription() {
        return "Mana energy profiling via intel's RAPL";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.Debugger.ThreadStates.Socket;
    }

    @Override
    public @NotNull
    @NonNls String getId() {
        return "ManaProfilingConfiguration";
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{ new ManaRaplConfigurationFactory(this) };
    }
}
