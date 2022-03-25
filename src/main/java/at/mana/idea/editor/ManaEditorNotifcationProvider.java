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
import at.mana.idea.util.ColorUtil;
import at.mana.idea.util.I18nUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaEditorNotifcationProvider extends EditorNotifications.Provider<EditorNotificationPanel> {

    private static final Key<String> KEY = new Key<>("ManaEditorNotification");

    @Override
    public @NotNull Key getKey() {
        return KEY;
    }

    @Override
    public @Nullable EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile( file );
        if( psiFile instanceof XmlFile) {
            XmlFile xmlFile = (XmlFile) psiFile;
            if( xmlFile.getName().equals( "pom.xml" )
                  && !ManaRaplConfigurationUtil.verifyMavenManaPluginAvailable( project, xmlFile )  ) {
                EditorNotificationPanel banner = new EditorNotificationPanel(ColorUtil.NOTIFICATION_COLOR);
                banner.text(I18nUtil.LITERALS.getString("notification.manaplugin"));
                banner.createActionLabel("Help", () -> {
                    // TODO: Open URL to documentation
                });
                return banner;
            }
        }
        return null;
    }
}
