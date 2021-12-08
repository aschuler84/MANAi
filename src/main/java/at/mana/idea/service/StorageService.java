package at.mana.idea.service;

import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.model.MethodEnergyModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface StorageService {

    static StorageService getInstance(@NotNull Project project) {
        return project.getService( StorageService.class);
    }

    public ManaEnergyExperimentModel findDataFor(PsiJavaFile file );

    public MethodEnergyModel findDataFor(PsiMethod method, VirtualFile file);

    void processAndStore( List<String> measurements);
}
