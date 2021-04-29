package at.mana.idea;

import at.mana.idea.service.ManaProjectService;
import at.mana.idea.domain.MethodEnergyStatistics;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ManaMethodEnergyAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if ( !( element instanceof PsiMethod ) ) {
            return;
        }

        Project project = element.getProject();
        ManaProjectService service = ServiceManager.getService(project,  ManaProjectService.class);
        PsiMethod method = (PsiMethod) element;
        MethodEnergyStatistics statistics = service.findStatisticsForMethod( method, element.getContainingFile().getVirtualFile() );
        if( statistics == null ) {
            return;
        }

        @NotNull AnnotationBuilder builder = holder.newAnnotation(
                HighlightSeverity.INFORMATION, "Method energy characteristics" )
                .range(method.getBody().getTextRange());
        // Force the text attributes to Simple syntax bad character
        TextAttributes textAttributes = new TextAttributes();
        textAttributes.setBackgroundColor( statistics.getHeatColor() );
        TextAttributesKey key = TextAttributesKey.createTextAttributesKey("manaAnnotator", textAttributes);
        builder.textAttributes( key ).create();
    }
}
