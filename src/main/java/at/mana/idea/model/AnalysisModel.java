package at.mana.idea.model;

import com.intellij.psi.PsiMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class AnalysisModel {

    private Map<PsiMethod, AnalysisModelComponent> components = new HashMap<>();

}
