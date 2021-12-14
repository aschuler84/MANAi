/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ManaService extends BulkFileListener {

    static ManaService getInstance(@NotNull Project project) {
        return project.getService( ManaService.class);
    }

    boolean isManaProject();

    List<VirtualFile> findAvailableManaFiles();

    void init();

}
