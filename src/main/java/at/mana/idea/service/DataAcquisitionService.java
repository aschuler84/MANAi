/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.service;

import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public interface DataAcquisitionService extends ProcessListener {

    static DataAcquisitionService getInstance(@NotNull Project project) {
        return project.getService( DataAcquisitionService.class);
    }

    void startDataAcquisition( @NotNull Project project );

}
