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
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ManaLineEditorPainter extends EditorLinePainter {
    @Override
    public @Nullable Collection<LineExtensionInfo> getLineExtensions(@NotNull Project project, @NotNull VirtualFile file, int lineNumber) {
        final Document doc = FileDocumentManager.getInstance().getDocument(file);
        ManaProjectService service = ServiceManager.getService(project,  ManaProjectService.class);

        if (doc == null || service == null ) {
            return null;
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile( file );
        if( psiFile instanceof PsiJavaFile) {
            PsiJavaFile javaFile = (PsiJavaFile) psiFile;
            List<ManaEnergyExperimentModel> statistics = service.findStatisticsFor( javaFile );
            final List<LineExtensionInfo> lines = new ArrayList<>();
            statistics.forEach( model -> {
                model.getMethodEnergyStatistics().forEach( (k,v)-> {
                    if( doc.getLineNumber( k.getTextOffset() ) == lineNumber ) {
                        String output = String.format( " %.2f|%.2f|%.2f|%.2f", v.getCpuWattage().getAverage(), v.getGpuWattage().getAverage(), v.getRamWattage().getAverage(), v.getOtherWattage().getAverage() );
                        lines.add( new LineExtensionInfo( output, JBColor.decode("0x54F756"), EffectType.BOXED, JBColor.YELLOW, Font.ITALIC) );
                    }
                });
            } );
            return lines;

        }
        return null;
    }
}
