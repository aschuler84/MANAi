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
import at.mana.idea.settings.ManaSettingsState;
import at.mana.idea.util.HibernateUtil;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaPluginStartup implements StartupActivity
{

    @Override
    public void runActivity(@NotNull Project project) {
        HibernateUtil.getSessionFactory();
        // verify if mana-instrument is installed
        if( !ManaSettingsState.getInstance().initialVerification ) {
        Notification notification = NotificationGroupManager.getInstance().getNotificationGroup("ManaNotificationGroup")
                .createNotification("MANAi verifies if the required plugins are installed",
                        NotificationType.INFORMATION);
                notification.addAction(NotificationAction.create( "Install Dependencies", anActionEvent -> {
                    notification.expire();
                    ManaRaplConfigurationUtil.installManaInstrumentPluginAvailable(anActionEvent.getProject(), new ProcessAdapter() {
                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            if( event.getExitCode() == 0 ) {
                                ManaSettingsState.getInstance().initialVerification = true;
                            } else {
                                NotificationGroupManager.getInstance().getNotificationGroup("ManaNotificationGroup")
                                        .createNotification("Could not install dependencies!", NotificationType.ERROR).notify(anActionEvent.getProject());
                            }
                        }
                    });
                } ))
                .notify(project);
        }
    }

}
