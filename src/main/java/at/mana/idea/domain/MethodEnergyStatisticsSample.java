package at.mana.idea.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MethodEnergyStatisticsSample {
    
    private long duration;
    private Double[] cpu;
    private Double[] gpu;
    private Double[] ram;
    private Double[] other;




}
