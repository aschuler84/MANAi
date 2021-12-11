/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.runner;

import at.mana.idea.configuration.ManaRaplJarConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ManaRaplProgramRunner extends GenericProgramRunner<ManaRaplProgramRunnerSettings> {
    public @NotNull
    @NonNls String getRunnerId() {
        return "manaRaplRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return profile instanceof ManaRaplJarConfiguration;
    }


    /*@Override
    public void execute(@NotNull ExecutionEnvironment environment) throws ExecutionException {
        //Notification notification = new Notification("ManaNotificationGroup", AllIcons.Debugger.ThreadStates.Socket, NotificationType.INFORMATION);
        //notification.setContent( "Mann RAPL Executor started" );
        //Notifications.Bus.notify(notification);
        Objects.requireNonNull( environment.getState() );
        ExecutionResult result = environment.getState().execute( environment.getExecutor(),this );
        new RunContentBuilder(result, environment).showRunContent(environment.getContentToReuse());
        //System.out.println( result );
        //ToolWindow toolWindow = ToolWindowManager.getInstance(environment.getProject()).getToolWindow("Run");
        //toolWindow.getContentManager().getFactory().createContent(result.getExecutionConsole().getComponent(), "Console", true);
    }*/

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        ExecutionResult result = Objects.requireNonNull(environment.getState()).execute( environment.getExecutor(),this );
        Objects.requireNonNull( result );
        return new RunContentBuilder(result, environment).showRunContent(environment.getContentToReuse());
    }
}
