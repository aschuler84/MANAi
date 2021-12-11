/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ComponentAdapter;

public class ManaRaplConfigurationEditor extends SettingsEditor<ManaRaplJarConfiguration> implements DumbAware {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JSpinner spinnerSamplesRecorded;
    private JSlider slideroNoSamples;
    private JTextField txtNoSamples;

    public ManaRaplConfigurationEditor() {
        slideroNoSamples.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                txtNoSamples.setText( slideroNoSamples.getValue() + "" );
            }
        });
    }

    @Override
    protected void resetEditorFrom(@NotNull ManaRaplJarConfiguration s) {
        this.slideroNoSamples.setValue( s.getSamplingRate() );
        this.txtNoSamples.setText( s.getSamplingRate() + "" );
        this.spinnerSamplesRecorded.setValue( s.getNoOfSamples() );
    }

    @Override
    protected void applyEditorTo(@NotNull ManaRaplJarConfiguration s) throws ConfigurationException {
        s.setNoOfSamples( (int) this.spinnerSamplesRecorded.getValue() ) ;
        s.setSamplingRate( this.slideroNoSamples.getValue() );
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return this.panel1;
    }
}
