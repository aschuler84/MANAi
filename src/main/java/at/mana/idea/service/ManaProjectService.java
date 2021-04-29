package at.mana.idea.service;

import at.mana.idea.domain.MethodEnergyStatistics;
import at.mana.idea.model.ManaEnergyExperimentModel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ManaProjectService extends BulkFileListener {
    static ManaProjectService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ManaProjectService.class);
    }

    MethodEnergyStatistics findStatisticsForMethod(PsiMethod method, VirtualFile file );

    List<ManaEnergyExperimentModel> findStatisticsFor(PsiJavaFile file );

    boolean isManaProject();

    List<VirtualFile> findAvailableManaFiles();

    void init();
}
