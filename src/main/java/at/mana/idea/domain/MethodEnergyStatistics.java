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
    private Long durationMillis;
    private JBColor heatColor = new JBColor(new Color(252, 43, 43, 60), new Color(252, 43, 43, 60));

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

    public Double getCpuWattage() {
        return samples.stream()
            .flatMap( sample -> Arrays.stream( sample.getCpu() ) ).collect( DoubleStatistics.collector() ).getAverage();
    }

    public Double getGpuWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getGpu() ) ).collect( DoubleStatistics.collector() ).getAverage();
    }

    public Double getRamWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getRam() ) ).collect( DoubleStatistics.collector() ).getAverage();
    }

    public Double getOtherWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getOther() ) ).collect( DoubleStatistics.collector() ).getAverage();
    }

    public void addSample( long durationMilliseconds, Double[] cpu, Double[] gpu, Double[] ram, Double[] other ) {
        this.samples.add( new MethodEnergyStatisticsSample( durationMilliseconds, cpu, gpu, ram, other ) );
    }


    public Double getTotal() {
        return this.getCpuWattage() + this.getGpuWattage() + this.getOtherWattage() + this.getRamWattage();
    }

}
