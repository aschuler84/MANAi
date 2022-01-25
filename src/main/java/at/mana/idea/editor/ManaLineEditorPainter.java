/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.editor;

import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.service.StorageService;
import at.mana.idea.util.ColorUtil;
import com.google.common.util.concurrent.AtomicDouble;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorLinePainter;
import com.intellij.openapi.editor.LineExtensionInfo;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class ManaLineEditorPainter extends EditorLinePainter implements UpdateInBackground {



    @Override
    public @Nullable Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        final Document doc = FileDocumentManager.getInstance().getDocument(file);
        StorageService service = StorageService.getInstance(project);

        if (doc == null || service == null ) {
            return null;
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile( file );
        if( psiFile instanceof PsiJavaFile && !psiFile.getFileType().getDefaultExtension().endsWith("class") ) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            ManaEnergyExperimentModel statistics =  ReadAction.compute( () ->  service.findDataFor( javaFile ) );
            if( statistics != null ) {
            final List<LineExtensionInfo> lines = new ArrayList<>();
            AtomicDouble total = new AtomicDouble(0);
            statistics.getMethodEnergyStatistics().forEach((key, value) -> total.addAndGet(value.stream().mapToDouble(m -> m.getEnergyConsumption().getAverage()).average().orElse(0.0)));
            //double total = statistics.getMethodEnergyStatistics().values().stream().flatMap(Collection::stream)
            //        .mapToDouble( value -> value.getEnergyConsumption().getAverage() ).sum();
                statistics.getMethodEnergyStatistics().forEach((k, v) -> {
                    if (doc.getLineNumber(k.getTextOffset()) == lineNumber) {
                        // compute max consumption in this class
                        // print relative contribution per each method as chart -> color coded
                        List<LineExtensionInfo> histogram = createHistogram( v );
                        Optional<MethodEnergyModel> oM = v.stream().max(Comparator.comparing(MethodEnergyModel::getStartDateTime));
                        if (oM.isPresent()) {
                            String line = "      \u2502";
                            String energyConsumption = String.format( "\u251C %.3fJ", oM.get().getEnergyConsumption().getAverage() );

                            lines.addAll(histogram);

                            lines.add(new LineExtensionInfo("    \u2502",ColorUtil.INLINE_TEXT, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
                            lines.add( createInLineChart( oM.get().getEnergyConsumption().getAverage()/total.get() ) );
                            lines.add(new LineExtensionInfo(energyConsumption, ColorUtil.INLINE_TEXT, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
                        }
                    }
                });
                return lines;
            }
        }
        return null;
    }

    private LineExtensionInfo createInLineChart( double fraction ) {
        StringBuilder chart = new StringBuilder();
        final int max = 5;
        //String[] color = new String[]{ "0x1d4877", "0x1b8a5a", "0xfbb021", "0xf68838", "0xee3e32"  };
        int index = (int) ( max * fraction );
        IntStream.range( 0, max ).forEach( i -> {
            if( i <= index ) {
                chart.append( "\u258B" );
            } else {
                chart.append( " " );
            }
        } );
        return new LineExtensionInfo(chart.toString(),
                ColorUtil.HEAT_MAP_COLORS_DEFAULT[Math.min(index, ColorUtil.HEAT_MAP_COLORS_DEFAULT.length)],
                EffectType.ROUNDED_BOX,
                JBColor.RED,
                Font.PLAIN);
    }

    private List<LineExtensionInfo> createHistogram( List<MethodEnergyModel> statistics ) {
        final int max = 5;
        double[] bins = new double[max];
        String[] elements = new String[]{
                "\u2581",
                "\u2582",
                "\u2583",
                "\u2584",
                "\u2585",
                "\u2586",
                "\u2587",
                "\u2588",
        };
        statistics.sort( Comparator.comparing( MethodEnergyModel::getStartDateTime ) );

        MethodEnergyModel newest = statistics.get( statistics.size() - 1 );
        MethodEnergyModel lowest = statistics.stream().filter( p -> !p.equals( newest ) ).min(
                    Comparator.comparingDouble(o -> o.getEnergyConsumption().getAverage())).orElse( newest );


        int binSize = statistics.size() / max;
        if( binSize == 0 ) {
            // reduce the number of bins
            bins = new double[statistics.size() % max ];
            binSize = 1;
        }
        int offset = 0;
        double maxV = 0;
        double sum = 0;
        for(int i = 0; i< bins.length; i++) {
            if( i + 1 == bins.length && offset+binSize < bins.length )
                binSize += bins.length - offset + binSize;
            bins[i] = statistics.subList( offset, offset+binSize ).stream().mapToDouble( m -> m.getEnergyConsumption().getAverage() ).average().orElse(0.0);
            sum += bins[i];
            maxV = Math.max( bins[i], maxV );
            offset = offset + binSize;
        };
        List<LineExtensionInfo> lines = new ArrayList<>();

        for(double entry : bins) {
            int index = (int) ( (elements.length - 1) * (entry / maxV));
            lines.add( new LineExtensionInfo( elements[index], ColorUtil.HEATMAP_COLORS_YLGNBU[5], EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN) );
        }

        if( lines.size() < max )
            IntStream.range( 0, max - lines.size() ).forEach( i -> lines.add(0, new LineExtensionInfo( " ", ColorUtil.HEAT_MAP_COLORS_DEFAULT[0], EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN) ) );

        lines.add(0,new LineExtensionInfo("      \u2502", ColorUtil.INLINE_TEXT, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
        lines.add(new LineExtensionInfo("\u251C ", ColorUtil.INLINE_TEXT, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));

        //lines.add(new LineExtensionInfo(String.format( "%.3fJ", sum/(1.0*bins.length)), ColorUtil.INLINE_TEXT, EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
        var rate = (newest.getEnergyConsumption().getAverage() - lowest.getEnergyConsumption().getAverage() ) / lowest.getEnergyConsumption().getAverage();
        if( rate == 0 ) {
            lines.add(new LineExtensionInfo(String.format( "%.2f%%", rate *100 ), ColorUtil.INLINE_TEXT_EVEN,EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
        } else if(  rate <= 0 ) {
            lines.add(new LineExtensionInfo(String.format( "\u2193 %.2f%%", rate *100 ), ColorUtil.INLINE_TEXT_DECREASE,EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
        } else {
            lines.add(new LineExtensionInfo(String.format( "\u2191 %.2f%%", rate *100 ), ColorUtil.INLINE_TEXT_INCREASE,EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
        }


        return lines;
    }



}
