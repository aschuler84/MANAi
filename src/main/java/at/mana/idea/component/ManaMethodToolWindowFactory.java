/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component;

import at.mana.idea.component.plot.*;
import at.mana.idea.model.AnalysisModel;
import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.model.MethodEnergySampleModel;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.service.AnalysisService;
import at.mana.idea.service.EnergyDataNotifierEvent;
import at.mana.idea.service.ManaEnergyDataNotifier;
import at.mana.idea.service.StorageService;
import at.mana.core.util.DoubleStatistics;
import at.mana.idea.util.ColorUtil;
import at.mana.idea.util.DateUtil;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.ui.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.TreePath;

import static  at.mana.idea.util.I18nUtil.i18n;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaMethodToolWindowFactory implements ToolWindowFactory {

    private ManaMethodToolWindow methodToolWindow;

    public static void initToolWindow(Project project) {
        ToolWindow window = ToolWindowManager.getInstance( project ).getToolWindow("Mana");
        if( window != null ) {
            Component component = window.getComponent().getComponent(0);
            if( component instanceof ManaMethodToolWindow ) {
                ManaMethodToolWindow toolWindow = (ManaMethodToolWindow) component;
                if (toolWindow.isVisible())
                    // initialize method tool window on startup
                    toolWindow.fillWindowFromModel(project);
            }
        }
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setTitle( "Mana" );
        toolWindow.getComponent().setBorder( JBUI.Borders.empty() );
        methodToolWindow = new ManaMethodToolWindow();
        toolWindow.getComponent().add(methodToolWindow.createContentComponent( project ) );
        // execute in background
        methodToolWindow.fillWindowFromModel( project );
    }

}
