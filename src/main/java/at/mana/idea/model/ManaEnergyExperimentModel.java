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

    /*
         Folder structure
            .mana (Class)
                - 09012021101010   (ddMMyyyyHHmmss) RUN  ManaEnergyExperimentModel (Samples per Experiment)
                    - at.mana.ManaInstrumentTest_testAlgorithmsSort.0.mana  (class_method)
                    - at.mana.ManaInstrumentTest_testAlgorithmsSort.1.mana  (class_method)
                    - at.mana.ManaInstrumentTest_testAlgorithmsSort.2.mana  (class_method)
                    - at.mana.ManaInstrumentTest_testAlgorithmsSort.3.mana  (class_method)
                - 09012021101011   (ddMMyyyyHHmmss) RUN ManaEnergyExperimentModel (Samples per Experiment)
                    - at.mana.ManaInstrumentTest_testAlgorithmsSort.0.mana  (class_method)
                    - at.mana.ManaInstrumentTest_testAlgorithmsSort.1.mana  (class_method)
                    - at.mana.ManaInstrumentTest_testAlgorithmsSort.2.mana  (class_method)
                    - at.mana.ManaInstrumentTest_testAlgorithmsSort.3.mana  (class_method)
     */


    private PsiClass experimentClass;
    private Map<PsiMethod, List<MethodEnergyStatistics>> methodEnergyStatistics = new HashMap<>();

}
