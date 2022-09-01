/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.editor;

import at.mana.idea.component.inline.SpectrumInlinePlotComponent;
import at.mana.idea.component.plot.SingleSpectrumPlotComponent;
import at.mana.idea.component.plot.SingleSpectrumPlotModel;
import at.mana.idea.model.AnalysisModel;
import at.mana.idea.model.AnalysisModelComponent;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.service.AnalysisService;
import at.mana.idea.service.StorageService;
import at.mana.idea.util.ColorUtil;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBInsets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class SpectrumLineEditorPainter extends EditorLinePainter implements UpdateInBackground {

    private Map<PsiMethod, SpectrumInlinePlotComponent> plotComponents = new HashMap<>();

    @Override
    public @Nullable Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        final Document doc = FileDocumentManager.getInstance().getDocument(file);
        AnalysisService service = AnalysisService.getInstance(project);

        if (doc == null || service == null ) {
            return null;
        }

        TextEditor editor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor(file);
        PsiFile psiFile = PsiManager.getInstance(project).findFile( file );
        if( psiFile instanceof PsiJavaFile && !psiFile.getFileType().getDefaultExtension().endsWith("class") ) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            AnalysisModel statistics =  ReadAction.compute( () ->  service.findDataFor( javaFile ) );
            if( statistics != null ) {
                /*plotComponents.entrySet().stream().filter( e -> statistics.getComponents().containsKey( e.getKey() ) ).forEach(e -> {
                    editor.getEditor().getContentComponent().remove( e.getValue() );
                } );*/
                final List<LineExtensionInfo> lines = new ArrayList<>();
                statistics.getComponents().forEach((k, v) -> {
                    if ( k.getTextOffset() < doc.getTextLength()
                            && doc.getLineNumber(k.getTextOffset()) == lineNumber) {

                            plotComponents.computeIfAbsent( k, m -> {
                                SpectrumInlinePlotComponent plot = new SpectrumInlinePlotComponent();
                                plot.getComponent().setBounds(
                                        editor.getEditor().offsetToVisualLine(doc.getLineEndOffset(lineNumber),false) + 30,
                                        editor.getEditor().visualLineToY( lineNumber), 250, editor.getEditor().getLineHeight() );
                                plot.updateModel( 0, new SingleSpectrumPlotModel( new double[]{v.getPowerCoefficient()} ) );
                                plot.updateModel( 1, new SingleSpectrumPlotModel( new double[]{v.getFrequencyCoefficient()} ) );
                                plot.updateModel( 2, new SingleSpectrumPlotModel( new double[]{v.getDurationCoefficient()} ) );
                                editor.getEditor().getContentComponent().add( plot.getComponent() );
                                return plot;
                            } );

                            if( !editor.getEditor().getContentComponent().equals( plotComponents.get(k) ) ) {
                                // if the label is already added to another component - we remove it
                                editor.getEditor().getContentComponent().add(plotComponents.get(k).getComponent());
                            }
                            //plotComponents.get( k ).setModel( new SingleSpectrumPlotModel( new double[]{0.45} ) );
                            Point offsetPoint = editor.getEditor().offsetToXY(doc.getLineEndOffset(lineNumber));
                            plotComponents.get(k).getComponent().setLocation(offsetPoint.x + 20,offsetPoint.y );
                                    //editor.getEditor().visualLineToY( lineNumber));

                            JBColor defaultColor = ColorUtil.INLINE_TEXT;
                            String caret = "";
                            if( service.hasSelectedMethod( k ) ) {
                                defaultColor = ColorUtil.INLINE_TEXT_HIGHLIGHTED;
                                caret = " \u142F";
                            }
                            /*String coefficient = String.format( "\u251C   %.3f (P) | %.3f (Fqn) | %.3f (T)" + caret, v.getPowerCoefficient(),v.getFrequencyCoefficient(), v.getDurationCoefficient() );
                            lines.add(new LineExtensionInfo("     \u2502",defaultColor, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
                            lines.add( createInLineChart( v.getPowerCoefficient() ) );
                            lines.add( new LineExtensionInfo("|",defaultColor, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
                            lines.add( createInLineChart( v.getFrequencyCoefficient()) );
                            lines.add( new LineExtensionInfo("|",defaultColor, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
                            lines.add( createInLineChart( v.getDurationCoefficient() ) );
                            lines.add(new LineExtensionInfo( coefficient, defaultColor, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN ));
                            */
                       } // else if(  ) method k not found

                });
                return lines;
            }
        }
        return null;
    }

    private JPanel createInlineChart(Editor editor, int x, int y, AnalysisModelComponent modelComponent ) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        SingleSpectrumPlotComponent plot = new SingleSpectrumPlotComponent();
        plot.setModel( new SingleSpectrumPlotModel( new double[]{modelComponent.getPowerCoefficient()} ) );
        plot.setInsets(JBInsets.create(0,0));
        plot.setDisplayLegend(false);
        plot.setBackground( new Color(0,0,0,0) );
        plot.setBounds(x + 10, y, 60, editor.getLineHeight() );
        panel.add( plot );
        return panel;
    }




    private LineExtensionInfo createInLineChart( double fraction ) {
        StringBuilder chart = new StringBuilder();
        final int max = 5;
        int index = (int) ( max * fraction );
        if( fraction > 0 ) {
            chart.append( "\u25A0" );
        } else {
            chart.append( "\u25A1" );
        }
        return new LineExtensionInfo(chart.toString(),
                ColorUtil.HEAT_MAP_COLORS_DEFAULT[Math.min(index, ColorUtil.HEAT_MAP_COLORS_DEFAULT.length-1)],
                EffectType.ROUNDED_BOX,
                JBColor.RED,
                Font.PLAIN);
    }


}
