package at.mana.idea.analysis;

import at.mana.core.util.DoubleStatistics;
import at.mana.idea.domain.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Andreas Schuler
 * @since 1.1
 * <br>
 * This class is a reimplementation of the approach
 * described by <em>Pereira</em> et al. from their JSS paper
 * <em>SPELLing out energy leaks: Aiding developers locate energy inefficient code</em><br>
 * <a href="https://dl.acm.org/doi/abs/10.1016/j.jss.2019.110463">https://dl.acm.org/doi/abs/10.1016/j.jss.2019.110463</a>
 */
public class SpellAnalysis {

    private static final int IDX_POWER = 0;
    private static final int IDX_FREQUENCY = 1;
    private static final int IDX_DURATION = 2;

    public Analysis compute(List<Measurement> measurement ) {
        Map<Measurement, List<SpellComponent>> matrix =
                measurement.stream().collect(Collectors.toMap( m -> m,
                            m -> m.getSamples()
                                    .stream()
                                    .flatMap( sample ->
                                            sample.getTrace().stream()
                                                    .collect( Collectors.groupingBy( Trace::getDescriptor ) )
                                                    .entrySet().stream()
                                                    .map( entry -> toAnalysisComponent(entry.getKey(), entry.getValue()) )
                                            )
                                    .collect(Collectors.toList() ) ) );

        var oracle = computeOracle( matrix );
        return computeSimilarity( matrix, oracle );
    }

    private Analysis computeSimilarity(Map<Measurement, List<SpellComponent>> matrix, Map<Measurement, SpellOracle> oracle) {
        Double[] totals = oracle.values().stream()
                .map( o -> new Double[]{ o.power, o.frequency, o.duration } )
                .reduce( this::reduce ).orElseThrow(  );
        Map<MemberDescriptor, Double[]> componentSum =
                matrix.entrySet().stream().flatMap(e -> e.getValue().stream())
                        .collect( Collectors.groupingBy(SpellComponent::getDescriptor) )
                        .entrySet().stream().map( e ->
                                        Map.entry( e.getKey(), e.getValue().stream().map( a ->
                                                        new Double[]{ a.getValues().get(IDX_POWER), a.getValues().get(IDX_FREQUENCY), a.getValues().get(IDX_DURATION) }
                                                         ).reduce( this::reduce ).map( a -> divide(a, totals) ).orElseThrow() )
                        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Analysis analysis = new Analysis();
        analysis.setCreated( LocalDateTime.now() );
        analysis.getComponents().addAll(
                componentSum.entrySet().stream()
                        .map( a -> new AnalysisComponent( a.getKey(), analysis, Arrays.asList(a.getValue()) ) )
                        .collect(Collectors.toList() ) );
        return analysis;
    }

    private Double[] reduce( Double[] a, Double[] b ) {
        var res = new Double[a.length];
        IntStream.range(0,a.length).forEach( i -> res[i] = a[i] + b[i] );
        return res;
    }

    private Double[] divide( Double[] a, Double[] b ) {
        var res = new Double[a.length];
        IntStream.range(0,a.length).forEach( i -> res[i] = a[i] / b[i] );
        return res;
    }

    private SpellComponent toAnalysisComponent( MemberDescriptor memberDescriptor, List<Trace> traces ) {
        DoubleStatistics powerCpu = traces.stream().flatMap( trace
                -> Arrays.stream( trace.getCpuPower() ) ).collect(DoubleStatistics.collector());
        DoubleStatistics powerRam = traces.stream().flatMap( trace
                -> Arrays.stream(trace.getRamPower() ) ).collect(DoubleStatistics.collector());
        DoubleStatistics powerGpu = traces.stream().flatMap( trace
                -> Arrays.stream(trace.getGpuPower() ) ).collect(DoubleStatistics.collector());
        DoubleStatistics powerOther = traces.stream().flatMap( trace
                -> Arrays.stream(trace.getOtherPower() ) ).collect(DoubleStatistics.collector());
        double frequency = traces.size();
        double duration = traces.stream().map(trace
                -> Duration.between( trace.getStart(), trace.getEnd() ).toMillis() ).mapToLong( l -> l ).average().orElse(0.0);
        var component = new SpellComponent(  );
        component.values =  List.of(powerCpu.getAverage() + powerRam.getAverage() + powerOther.getAverage() + powerGpu.getAverage(), frequency, duration );
        component.descriptor =  memberDescriptor;
        return component;
    }

    private Map<Measurement, SpellOracle> computeOracle(Map<Measurement, List<SpellComponent>> matrix ) {
        return matrix.entrySet().stream().map(
                e ->  Map.entry( e.getKey(), new SpellOracle(e.getValue())) )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static class SpellOracle {
        private Double power = 0.0;
        private Double frequency = 0.0;
        private Double duration = 0.0;

        public SpellOracle(List<SpellComponent> values ) {
            values.forEach(a -> {
                    power += a.values.get( IDX_POWER );
                    frequency += a.values.get( IDX_FREQUENCY );
                    duration += a.values.get( IDX_DURATION );
            } );
        }
    }

    private static class SpellComponent {
        private MemberDescriptor descriptor;
        private List<Double> values;

        public MemberDescriptor getDescriptor() {
            return descriptor;
        }

        public void setDescriptor(MemberDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        public List<Double> getValues() {
            return values;
        }

        public void setValues(List<Double> values) {
            this.values = values;
        }
    }

}
