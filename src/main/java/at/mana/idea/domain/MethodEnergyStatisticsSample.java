package at.mana.idea.domain;

import at.mana.idea.util.DoubleStatistics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class MethodEnergyStatisticsSample {
    
    private double duration;
    private double energyConsumption;
    private Double[] cpu;
    private Double[] gpu;
    private Double[] ram;
    private Double[] other;

    public MethodEnergyStatisticsSample(double duration, Double[] cpu, Double[] gpu, Double[] ram, Double[] other) {
        this.duration = duration;
        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;
        this.other = other;
        this.energyConsumption = this.duration * ( getCpuWattage().getAverage() + getGpuWattage().getAverage() + getRamWattage().getAverage() + getOtherWattage().getAverage() );
    }

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
