/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.editor;

import at.mana.idea.configuration.ManaRaplConfigurationUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.TestFrameworkUtil;
import com.intellij.testIntegration.TestFramework;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ManaEditorNotifcationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {

    private static final Key<String> KEY = new Key<>("ManaEditorNotification");

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @Nullable EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile( file );
        if( psiFile instanceof PsiJavaFile) {
            PsiJavaFile clazz = (PsiJavaFile) psiFile;
            if( clazz.getName().contains( "Test" ) ) {
                if( ManaRaplConfigurationUtil.findExecutablePath( ManaRaplConfigurationUtil.RAPL_HOME_KEY, ManaRaplConfigurationUtil.RAPL_EXECUTABLE_NAME ) == null ) {
                    EditorNotificationPanel banner = new EditorNotificationPanel(new JBColor(new Color(237, 180, 180), new Color(237, 180, 180)));
                    banner.text("Please specify the RAPL_HOME environment variable");
                    banner.createActionLabel("Configure environment variable", () -> {
                        ShowSettingsUtil.getInstance().showSettingsDialog(project, "Path Variables");
                    });
                    return banner;
                }
            }
        } else if( psiFile instanceof XmlFile) {
            XmlFile xmlFile = (XmlFile) psiFile;
            if( xmlFile.getName().equals( "pom.xml" )
                  && !ManaRaplConfigurationUtil.verifyMavenManaPluginAvailable( project, xmlFile )  ) {
                EditorNotificationPanel banner = new EditorNotificationPanel(new JBColor(new Color(237, 180, 180), new Color(237, 180, 180)));
                banner.text("To use Mana RAPL, please include the Mana maven instrument plugin in you pom.xml");
                banner.createActionLabel("Help", () -> {
                    // TODO: Open URL to documentation
                });
                return banner;
            }
        }
        return null;
    }
}
