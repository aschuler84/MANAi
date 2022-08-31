/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea;

import at.mana.idea.component.ManaMethodToolWindow;
import at.mana.idea.component.ManaMethodToolWindowFactory;
import at.mana.idea.component.plot.SingleSpectrumPlotComponent;
import at.mana.idea.component.plot.SingleSpectrumPlotModel;
import at.mana.idea.configuration.ManaRaplConfigurationUtil;
import at.mana.idea.listener.EditorHoverListener;
import at.mana.idea.model.AnalysisModel;
import at.mana.idea.service.AnalysisService;
import at.mana.idea.service.EnergyDataNotifierEvent;
import at.mana.idea.service.ManaEnergyDataNotifier;
import at.mana.idea.settings.ManaSettingsState;
import at.mana.idea.util.HibernateUtil;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import java.awt.*;
import java.util.Arrays;

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
        EditorHoverListener hl = new EditorHoverListener();
        EditorFactory.getInstance().getEventMulticaster().addEditorMouseListener(hl );
        EditorFactory.getInstance().getEventMulticaster().addEditorMouseMotionListener(hl);

        HibernateUtil.getSessionFactory();
        ManaMethodToolWindowFactory.initToolWindow(project);

        //ManaMethodToolWindow methodToolWindow = (ManaMethodToolWindow) window.getContentManager().getComponent();

        // TODO: uncomment if required - Advanced inline charts
        /*final AnalysisService service = AnalysisService.getInstance(project);
        Arrays.stream(FileEditorManager.getInstance(project).getAllEditors()).forEach( e -> {
            if( e instanceof  TextEditor ) {
                TextEditor editor = (TextEditor) e;
                final Document doc = editor.getEditor().getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(doc);
                if( psiFile != null && psiFile instanceof PsiJavaFile && !psiFile.getFileType().getDefaultExtension().endsWith("class") ) {
                    PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                    AnalysisModel statistics =  ReadAction.compute( () ->  service.findDataFor( javaFile ) );
                    statistics.getComponents().forEach((k, v) -> {
                        var plot =  new SingleSpectrumPlotComponent();
                        plot.setModel( new SingleSpectrumPlotModel( new double[]{0.45} ) );
                        plot.setBackground( new Color(0,0,0,0));
                        plot.setBounds(editor.getEditor().offsetToVisualLine(k.getTextOffset(),false) + 400,
                                editor.getEditor().visualLineToY( doc.getLineNumber(k.getTextOffset())), 70, editor.getEditor().getLineHeight() );

                        SwingUtilities.invokeLater( () -> editor.getEditor().getContentComponent().add( plot ) );

                    });
                }
            }
        } );*/

        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener() {
            @Override
            public void editorCreated(@NotNull EditorFactoryEvent event) {
                /*final Document doc = event.getEditor().getDocument();
                    AnalysisService service = AnalysisService.getInstance(project);
                    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(doc);
                    if( psiFile instanceof PsiJavaFile && !psiFile.getFileType().getDefaultExtension().endsWith("class") ) {
                        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                        AnalysisModel statistics =  ReadAction.compute( () ->  service.findDataFor( javaFile ) );
                        statistics.getComponents().forEach((k, v) -> {
                            var plot =  new SingleSpectrumPlotComponent();
                            plot.setModel( new SingleSpectrumPlotModel( new double[]{0.45} ) );
                            plot.setBackground( new Color(0,0,0,0));
                            plot.setBounds(event.getEditor().offsetToVisualLine(k.getTextOffset(),false) + 400,
                                    event.getEditor().visualLineToY( doc.getLineNumber(k.getTextOffset())), 200, 30 );

                            SwingUtilities.invokeLater( () -> event.getEditor().getContentComponent().add( plot ) );

                        });
                    }*/
            }
        }, project);

        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {

            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                // TODO: uncomment if required - Advanced inline charts
                /*if (event.getNewFile() != null) {
                    final Document doc = FileDocumentManager.getInstance().getDocument(event.getNewFile());
                    TextEditor editor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor(event.getNewFile());
                    AnalysisService service = AnalysisService.getInstance(project);
                    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(doc);
                    if (psiFile instanceof PsiJavaFile && !psiFile.getFileType().getDefaultExtension().endsWith("class")) {
                        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                        AnalysisModel statistics = ReadAction.compute(() -> service.findDataFor(javaFile));
                        if( statistics.getComponents() != null ) {
                            statistics.getComponents().forEach((k, v) -> {
                                var plot = new SingleSpectrumPlotComponent();
                                plot.setModel(new SingleSpectrumPlotModel(new double[]{0.45}));
                                plot.setBackground(new Color(0, 0, 0, 0));
                                plot.setDisplayLegend(false);
                                plot.setBounds(editor.getEditor().offsetToVisualLine(k.getTextOffset(), false) + 400,
                                        editor.getEditor().visualLineToY(doc.getLineNumber(k.getTextOffset())), 70, editor.getEditor().getLineHeight());

                                SwingUtilities.invokeLater(() -> editor.getEditor().getContentComponent().add(plot));

                            });
                        }
                    }
                }*/
            }
        });

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
