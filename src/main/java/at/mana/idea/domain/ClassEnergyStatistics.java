package at.mana.idea.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClassEnergyStatistics {

    private String className;
    private String packageName;
    private double energyConsumption;
    private int numberOfMethods;
}
