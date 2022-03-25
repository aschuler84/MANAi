/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaRaplRunConfigurationProducer extends LazyRunConfigurationProducer<ManaRaplJarConfiguration> {

    @Override
    protected boolean setupConfigurationFromContext( @NotNull ManaRaplJarConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        if( sourceElement.get().getContainingFile() instanceof PsiJavaFile
                && sourceElement.get() instanceof PsiClass ){
            PsiClass clazz = (PsiClass) sourceElement.get();
            configuration.setSelectedClass( clazz.getQualifiedName() );
            configuration.setName( clazz.getName() );
            return true;
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull ManaRaplJarConfiguration configuration, @NotNull ConfigurationContext context) {
        return true;
    }

    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return new ManaRaplConfigurationFactory(new ManaRaplConfigurationType());
    }
}
