package at.mana.idea.service;

import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.model.ManaEnergyExperimentModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ManaService extends BulkFileListener {

    static ManaService getInstance(@NotNull Project project) {
        return project.getService( ManaService.class);
    }

    boolean isManaProject();

    List<VirtualFile> findAvailableManaFiles();

    void init();

}
