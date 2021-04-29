package at.mana.idea.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ManaRaplConfigurationFactory extends ConfigurationFactory {

    private static final String MANA_RAPL_FACTORY_NAME = "Mana Profiling Configuration Factory";

    public ManaRaplConfigurationFactory(ManaRaplConfigurationType manaRaplConfigurationType) {
        super(manaRaplConfigurationType);

    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new ManaRaplConfiguration(project, this, "Mana");
    }

    @Override
    public @NotNull @Nls String getName() {
        return MANA_RAPL_FACTORY_NAME;
    }
}
