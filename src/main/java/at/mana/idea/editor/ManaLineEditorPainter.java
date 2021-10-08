package at.mana.idea.editor;

import at.mana.idea.domain.MethodEnergyStatistics;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.service.ManaProjectService;
import com.intellij.openapi.components.ServiceManager;
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
import org.apache.batik.css.engine.value.css2.FontStyleManager;
import org.eclipse.xtext.xbase.lib.IntegerRange;
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class ManaLineEditorPainter extends EditorLinePainter {

    private final Color[] colors = new Color[] {
            new JBColor(new Color(138,153,212), new Color(138,153,212)),
            new JBColor(new Color(120,184,174), new Color(120,184,174)),
            new JBColor(new Color(154,197,166), new Color(154,197,166)),
            new JBColor(new Color(255,173,138), new Color(255,173,138)),
            new JBColor(new Color(255, 143, 143), new Color(255, 143, 143)),
    };

    @Override
    public @Nullable Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        final Document doc = FileDocumentManager.getInstance().getDocument(file);
        ManaProjectService service = ServiceManager.getService(project, ManaProjectService.class);

        if (doc == null || service == null ) {
            return null;
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile( file );
        if( psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            ManaEnergyExperimentModel statistics = service.findStatisticsFor( javaFile );
            if( statistics != null ) {
            final List<LineExtensionInfo> lines = new ArrayList<>();
            double total = statistics.getMethodEnergyStatistics().values().stream().flatMap(Collection::stream)
                    .mapToDouble( value -> value.getEnergyConsumption().getAverage() ).sum();
                statistics.getMethodEnergyStatistics().forEach((k, v) -> {
                    if (doc.getLineNumber(k.getTextOffset()) == lineNumber) {
                        // compute max consumption in this class
                        // print relative contribution per each method as chart -> color coded

                        Optional<MethodEnergyStatistics> oM = v.stream().max(Comparator.comparing(MethodEnergyStatistics::getRecorded));
                        if (oM.isPresent()) {
                            String line = "      \u2502";
                            String energyConsumption = String.format( "\u251C %.3fJ", oM.get().getEnergyConsumption().getAverage() );
                            lines.add(new LineExtensionInfo(line, JBColor.decode("0x999999"), EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
                            lines.add(new LineExtensionInfo("\u2581\u2582\u2583\u2584\u2585\u2586\u2587\u2588", JBColor.decode("0x1b8a5a"), EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
                            lines.add(new LineExtensionInfo("\u2502 \u2502",JBColor.decode("0x999999"), EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
                            lines.add( createInLineChart( oM.get().getEnergyConsumption().getAverage()/total ) );
                            lines.add(new LineExtensionInfo(energyConsumption, JBColor.decode("0x999999"), EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN));
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
        return new LineExtensionInfo(chart.toString(), colors[index], EffectType.ROUNDED_BOX, JBColor.RED, Font.PLAIN);
    }

}
