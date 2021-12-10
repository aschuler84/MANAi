package at.mana.idea;

import at.mana.idea.service.ManaService;
import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.service.StorageService;
import at.mana.idea.service.StorageServiceImpl;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public class ManaMethodEnergyAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if ( !( element instanceof PsiMethod ) &&
             !( element.getContainingFile() instanceof PsiJavaFile )   ) {
            return;
        }

        Project project = element.getProject();
        StorageService service = StorageService.getInstance(project);
        PsiMethod method = (PsiMethod) element;
        MethodEnergyModel statistics = service.findDataFor( method, (PsiJavaFile) element.getContainingFile() );
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
