package at.mana.idea.service;

import com.intellij.openapi.project.Project;
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


}
