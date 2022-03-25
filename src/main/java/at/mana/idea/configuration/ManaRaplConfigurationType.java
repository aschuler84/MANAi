/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import at.mana.idea.Icons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
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
        return Icons.LOGO;
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
