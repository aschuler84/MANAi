/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import java.awt.*;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaMethodToolWindowFactory implements ToolWindowFactory {

    public static void initToolWindow(Project project) {
        ToolWindow window = ToolWindowManager.getInstance( project ).getToolWindow("Mana");
        if( window != null && window.getComponent().getComponentCount() > 0 ) {
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
        ManaMethodToolWindow methodToolWindow = new ManaMethodToolWindow();
        toolWindow.getComponent().add(methodToolWindow.createContentComponent( project ) );
        // execute in background
        methodToolWindow.fillWindowFromModel( project );
    }

}
