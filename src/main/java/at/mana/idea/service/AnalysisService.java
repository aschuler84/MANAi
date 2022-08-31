package at.mana.idea.service;

import at.mana.idea.model.AnalysisModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public interface AnalysisService {

    static AnalysisService getInstance(@NotNull Project project) {
        return project.getService( AnalysisService.class);
    }

    public void analyze( Project project );

    public AnalysisModel findDataFor(PsiJavaFile file);

    void setSelectedMethod( PsiMethod method );

    void clearSelectedMethod(  );

    boolean hasSelectedMethod( PsiMethod method );

}
