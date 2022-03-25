/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaTraceFileEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getExtension() != null && file.getExtension().equals( "mana" );
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new ManaTraceFileEditor( file );
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return "mana";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }

}
