package at.mana.idea.domain;

import at.mana.idea.util.DoubleStatistics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
public class MethodEnergyStatisticsSample {
    
    private long duration;
    private Double[] cpu;
    private Double[] gpu;
    private Double[] ram;
    private Double[] other;

    public DoubleStatistics getCpuWattage() {
        return Arrays.stream( cpu ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getGpuWattage() {
        return Arrays.stream( gpu ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getRamWattage() {
        return Arrays.stream( ram ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getOtherWattage() {
        return Arrays.stream( other ).collect( DoubleStatistics.collector() );
    }

}
