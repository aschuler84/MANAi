/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component;

import at.mana.idea.component.details.ClassEditorNamedConfigurable;
import at.mana.idea.component.details.MethodEditorNamedConfigurable;
import at.mana.idea.component.details.SummaryEditorNamedConfigurable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.roots.ui.configuration.projectRoot.TextConfigurable;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.Comparator;

public class ManaTraceFileEditor implements FileEditor {

    private ManaMasterDetailView component;
    private JComponent comp;
    private VirtualFile file;

    public ManaTraceFileEditor(VirtualFile file) {
        this.component = new ManaMasterDetailView();
        this.comp = component.createComponent();
        this.file = file;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return comp;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Override
    public @NotNull String getName() {
        return "Mana Trace File Editor";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }

    private class ManaMasterDetailView extends MasterDetailsComponent {

        public ManaMasterDetailView() {
            this.addNode( new MyNode( new MethodEditorNamedConfigurable( file, "Method Overview", "Overview on the energy consumption of the traced methods." ) ), myRoot );
            this.addNode( new MyNode( new SummaryEditorNamedConfigurable( file, "Summary", "Overview on the energy consumption data of the selected file." ) ), myRoot );
            this.addNode( new MyNode( new ClassEditorNamedConfigurable( file, "Class Overview", "Overview on the energy consumption of the traced classes." ) ), myRoot );
            selectNodeInTree( this.myRoot.getChildAt(0) );
            this.initTree();
        }

        @Override
        public @NlsContexts.ConfigurableName String getDisplayName() {
            return "Mana Trace File Editor";
        }



    }

}
