package at.mana.idea.domain;

import com.intellij.psi.PsiMethod;
import com.intellij.ui.JBColor;

import at.mana.idea.util.DoubleStatistics;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Setter
public class MethodEnergyStatistics {

    private PsiMethod method;
    private Double durationMillis;
    private JBColor heatColor = new JBColor(JBColor.decode("0xD8F0E8"),JBColor.decode("0xD8F0E8"));
    private Map<LocalDateTime, Set<MethodEnergyStatisticsSample>> samples = new HashMap<>();

    public MethodEnergyStatistics( PsiMethod method ) {
        this.method = method;
    }

    public DoubleStatistics getCpuWattageLatest() {
        LocalDateTime date = samples.keySet().stream().max(LocalDateTime::compareTo).orElse( null );
        return date != null ? samples.get( date ).stream()
                .flatMap( sample -> Arrays.stream( sample.getCpu() ) ).collect( DoubleStatistics.collector() ) : null;
    }

    private DoubleStatistics getValue(Function<MethodEnergyStatisticsSample,Double[]> map ) {
        return samples.values().stream()
                .flatMap(Collection::stream)
                .map( map )
                .flatMap( Arrays::stream )
                .collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getCpuWattage() {
        return getValue( MethodEnergyStatisticsSample::getCpu );
    }

    public DoubleStatistics getGpuWattage() {
        return getValue( MethodEnergyStatisticsSample::getGpu );
    }

    public DoubleStatistics getRamWattage() {
        return getValue( MethodEnergyStatisticsSample::getRam );
    }

    public DoubleStatistics getOtherWattage() {
        return getValue( MethodEnergyStatisticsSample::getOther );
    }

    public DoubleStatistics getDuration() {
        return samples.values().stream()
                .flatMap( Collection::stream )
                .map( MethodEnergyStatisticsSample::getDuration )
                .collect( DoubleStatistics.collector() );
    }

    public void addSample( LocalDateTime recorded, long durationMilliseconds, Double[] cpu, Double[] gpu, Double[] ram, Double[] other ) {
        this.samples.computeIfAbsent( recorded, localDateTime -> new HashSet<>() );
        this.samples.get(recorded).add( new MethodEnergyStatisticsSample( durationMilliseconds / 1000.0, cpu, gpu, ram, other ) );
    }

    public Double getTotal() {
        return this.getCpuWattage().getAverage() + this.getGpuWattage().getAverage() + this.getOtherWattage().getAverage() + this.getRamWattage().getAverage();
    }

}
