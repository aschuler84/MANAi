/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaRaplConfigurationFactory extends ConfigurationFactory {

    private static final String MANA_RAPL_FACTORY_NAME = "Mana Profiling Configuration Factory";

    public ManaRaplConfigurationFactory(ManaRaplConfigurationType manaRaplConfigurationType) {
        super(manaRaplConfigurationType);

    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        //return new ManaRaplConfiguration(project, this, "Mana");
        return new ManaRaplJarConfiguration("Mana", project, this);
    }

    @Override
    public @NotNull @Nls String getName() {
        return MANA_RAPL_FACTORY_NAME;
    }


    @Override
    public @NotNull
    @NonNls String getId() {
        return getName();
    }

    @Override
    public @Nullable Class<? extends BaseState> getOptionsClass() {
        return ManaRaplConfigurationOptions.class;
    }
}
