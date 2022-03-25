/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.runner;

import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaRaplProgramRunnerSettings implements RunnerSettings {
    @Override
    public void readExternal(Element element) throws InvalidDataException {

    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {

    }
}
