/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.details;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaEditorClassDetailsComponent extends ManaEditorDetailsComponent {

    public ManaEditorClassDetailsComponent(VirtualFile file, String title, String description) {
        super(file, title, description);
    }

    @Override
    protected JComponent createContent() {
        return new JPanel();
    }
}
