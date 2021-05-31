package at.mana.idea.domain;

import com.intellij.psi.PsiMethod;
import com.intellij.ui.JBColor;

import at.mana.idea.util.DoubleStatistics;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class MethodEnergyStatistics {

    private PsiMethod method;
    private LocalDateTime recorded;
    //private DoubleStatistics coreWattage;
    //private DoubleStatistics gpuWattage;
    //private DoubleStatistics otherWattage;
    //private DoubleStatistics ramWattage;
    private Double durationMillis;
    private JBColor heatColor = new JBColor(JBColor.decode("0xD8F0E8"),JBColor.decode("0xD8F0E8"));

    private Set<MethodEnergyStatisticsSample> samples = new HashSet<>(); 


    public MethodEnergyStatistics( LocalDateTime recorded, PsiMethod method ) {
        //coreWattage = Arrays.stream(core).collect(DoubleStatistics.collector());
        //gpuWattage = Arrays.stream(gpu).collect(DoubleStatistics.collector());
        //otherWattage = Arrays.stream(other).collect(DoubleStatistics.collector());
        //ramWattage = Arrays.stream(ram).collect(DoubleStatistics.collector());
        //this.durationMillis = durationMilliseconds;
        this.recorded = recorded;
        this.method = method;
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
        return  samples.stream().map(MethodEnergyStatisticsSample::getDuration).collect( DoubleStatistics.collector() );
    }


    public void addSample( long durationMilliseconds, Double[] cpu, Double[] gpu, Double[] ram, Double[] other ) {
        this.samples.add( new MethodEnergyStatisticsSample( durationMilliseconds / 1000.0, cpu, gpu, ram, other ) );
    }


    public Double getTotal() {
        return this.getCpuWattage().getAverage() + this.getGpuWattage().getAverage() + this.getOtherWattage().getAverage() + this.getRamWattage().getAverage();
    }

}
