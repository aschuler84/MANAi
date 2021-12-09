package at.mana.idea.model;

import at.mana.core.util.DoubleStatistics;
import com.intellij.ui.JBColor;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class MethodEnergyModel {

    private LocalDateTime recorded;
    private JBColor heatColor = new JBColor(JBColor.decode("0xD8F0E8"),JBColor.decode("0xD8F0E8"));
    private Set<MethodEnergySampleModel> samples = new HashSet<>();


    public MethodEnergyModel(LocalDateTime recorded ) {
        this.recorded = recorded;
    }

    public DoubleStatistics getCpuWattage() {
        return samples.stream()
            .flatMap( sample -> Arrays.stream( sample.getCpu() ) ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getGpuWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getGpu() ) ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getRamWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getRam() ) ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getOtherWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getOther() ) ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getDuration() {
        return  samples.stream().map(MethodEnergySampleModel::getDuration).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getEnergyConsumption() {
        return samples.stream().map(MethodEnergySampleModel::getEnergyConsumption).collect( DoubleStatistics.collector() );
    }

    public void addSample( long durationMilliseconds, Double[] cpu, Double[] gpu, Double[] ram, Double[] other ) {
        this.samples.add( new MethodEnergySampleModel( durationMilliseconds / 1000.0, cpu, gpu, ram, other ) );
    }


    public Double getTotal() {
        return this.getCpuWattage().getAverage() + this.getGpuWattage().getAverage() + this.getOtherWattage().getAverage() + this.getRamWattage().getAverage();
    }

}
