/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.method;

import at.mana.idea.model.MethodEnergyModel;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MethodStringColumn<T> extends ColumnInfo<MethodEnergyModel, T> {

    private final Function<MethodEnergyModel, T> mapper;
    private final boolean numberDecorated;

    public MethodStringColumn(@NlsContexts.ColumnName String name, Function<MethodEnergyModel, T> mapper, boolean numberDecorated) {
        super(name);
        this.numberDecorated = numberDecorated;
        this.mapper = mapper;
    }

    @Override
    public @Nullable T valueOf(MethodEnergyModel classEnergyStatistics) {
        return mapper.apply( classEnergyStatistics );
    }

}
