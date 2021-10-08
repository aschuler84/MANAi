package at.mana.idea.configuration;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class ManaRaplRunConfigurationProducer extends LazyRunConfigurationProducer<ManaRaplConfiguration> {

    @Override
    protected boolean setupConfigurationFromContext(@NotNull ManaRaplConfiguration configuration, @NotNull ConfigurationContext context, @NotNull Ref<PsiElement> sourceElement) {
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull ManaRaplConfiguration configuration, @NotNull ConfigurationContext context) {
        return true;
    }

    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return new ManaRaplConfigurationFactory(new ManaRaplConfigurationType());
    }
}
