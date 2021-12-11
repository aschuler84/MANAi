/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.details;

import at.mana.idea.component.plot.TimeSeriesPlotComponent;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SummaryEditorNamedConfigurable extends ManaEditorNamedConfigurable {

    private ManaEditorSummaryDetailsComponent summaryDetailsComponent;
    private JComponent summaryComponent;

    public SummaryEditorNamedConfigurable(VirtualFile file, String displayName, String description) {
        super(file, displayName, description);
    }

    protected void init() {
        this.summaryDetailsComponent = new ManaEditorSummaryDetailsComponent( this.file, this.displayName, this.description );
        this.summaryComponent = this.summaryDetailsComponent.createComponent();
    }
    @Override
    public JComponent createOptionsPanel() {
        //this.timeSeriesPlot.update();
        return this.summaryComponent;
    }

    @Override
    public @Nullable Icon getIcon(boolean expanded) {
        return AllIcons.Actions.ListFiles;
    }
}
