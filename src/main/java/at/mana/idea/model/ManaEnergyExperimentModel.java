package at.mana.idea.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ManaEnergyExperimentModel {

    private PsiFile experimentFile;
    private Map<PsiMethod, List<MethodEnergyModel>> methodEnergyStatistics = new HashMap<>();

}
