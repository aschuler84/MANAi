/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.details;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class ManaEditorNamedConfigurable extends NamedConfigurable<VirtualFile> {


    protected String displayName;
    protected String description;
    protected final VirtualFile file;

    public ManaEditorNamedConfigurable( VirtualFile file, String displayName, String description ) {
        this.displayName = displayName;
        this.description = description;
        this.file = file;
        init();
    }

    protected abstract void init();

    @Override
    public void setDisplayName(String name) {
        this.displayName = name;
        this.init();
    }

    @Override
    public VirtualFile getEditableObject() {
        return this.file;
    }

    @Override
    public String getBannerSlogan() {
        return this.description;
    }

    @Override
    public abstract JComponent createOptionsPanel();

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return this.displayName;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public abstract @Nullable Icon getIcon(boolean expanded);
}
