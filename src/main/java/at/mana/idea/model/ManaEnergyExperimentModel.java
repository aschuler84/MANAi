package at.mana.idea.model;

import at.mana.idea.domain.MethodEnergyStatistics;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Aggregates per class Energy statistics
 * Class -> FileEnergyStatsModel
 *             Method -> MethodEnergyStatistics
 *                        -> MethodEnergyStatisticsSample
 */

@Getter
@Setter
public class ManaEnergyExperimentModel {


    private PsiClass experimentClass;
    private Map<PsiMethod, List<MethodEnergyStatistics>> methodEnergyStatistics = new HashMap<>();

}
