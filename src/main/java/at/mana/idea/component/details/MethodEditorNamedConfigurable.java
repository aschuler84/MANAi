/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.details;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MethodEditorNamedConfigurable extends ManaEditorNamedConfigurable{

    private ManaEditorMethodDetailsComponent methodDetails;
    private JComponent component;

    public MethodEditorNamedConfigurable(VirtualFile file, String displayName, String description) {
        super(file, displayName, description);
    }

    @Override
    protected void init() {
        this.methodDetails = new ManaEditorMethodDetailsComponent( this.file, this.displayName, this.description );
        this.component  = this.methodDetails.createComponent();
    }

    @Override
    public JComponent createOptionsPanel() {
        return this.component;
    }

    @Override
    public @Nullable Icon getIcon(boolean expanded) {
        return AllIcons.Nodes.Method;
    }
}
