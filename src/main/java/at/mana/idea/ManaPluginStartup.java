/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea;

import at.mana.idea.configuration.ManaRaplConfigurationUtil;
import at.mana.idea.listener.HoverListener;
import at.mana.idea.settings.ManaSettingsState;
import at.mana.idea.util.HibernateUtil;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static  at.mana.idea.util.I18nUtil.i18n;
/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaPluginStartup implements StartupActivity
{

    @Override
    public void runActivity(@NotNull Project project) {
        // Install Editor Listeners
        HoverListener hl = new HoverListener();
        EditorFactory.getInstance().getEventMulticaster().addEditorMouseListener(hl );
        EditorFactory.getInstance().getEventMulticaster().addEditorMouseMotionListener(hl);
        HibernateUtil.getSessionFactory();

        if( !ManaSettingsState.getInstance().initialVerification ) {
            // TODO: extract to configuration util
            Notification notification = NotificationGroupManager.getInstance().getNotificationGroup("ManaNotificationGroup")
                .createNotification(i18n( "notification.dependencies" ), NotificationType.INFORMATION)
                .setIcon( Icons.LOGO_GUTTER );
                notification.addAction(NotificationAction.create( "Install Dependencies", anActionEvent -> {
                    notification.expire();
                    ManaRaplConfigurationUtil.installManaInstrumentPluginAvailable(anActionEvent.getProject(), new ProcessAdapter() {
                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            if( event.getExitCode() == 0 ) {
                                ManaSettingsState.getInstance().initialVerification = true;
                            } else {
                                NotificationGroupManager.getInstance().getNotificationGroup("ManaNotificationGroup")
                                        .createNotification(
                                                i18n("notification.dependencies.failure"),
                                                NotificationType.ERROR).notify(anActionEvent.getProject());
                            }
                        }
                        @Override
                        public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                            System.out.println( event.getText() );
                        }
                    });
                } )).notify(project);
        }
    }

}
