package at.mana.idea.domain;

import com.intellij.psi.PsiMethod;
import com.intellij.ui.JBColor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collector;

@Getter
@Setter
public class MethodEnergyStatistics {

    private PsiMethod method;
    private LocalDateTime recorded;
    private DoubleStatistics coreWattage;
    private DoubleStatistics gpuWattage;
    private DoubleStatistics otherWattage;
    private DoubleStatistics ramWattage;
    private Long durationMillis;
    private JBColor heatColor = new JBColor(new Color(252, 43, 43, 60), new Color(252, 43, 43, 60));


    public MethodEnergyStatistics( LocalDateTime recorded, PsiMethod method, long durationMilliseconds,
                                   Double[] core, Double[] gpu, Double[] other, Double[] ram ) {
        coreWattage = Arrays.stream(core).collect(DoubleStatistics.collector());
        gpuWattage = Arrays.stream(gpu).collect(DoubleStatistics.collector());
        otherWattage = Arrays.stream(other).collect(DoubleStatistics.collector());
        ramWattage = Arrays.stream(ram).collect(DoubleStatistics.collector());
        this.durationMillis = durationMilliseconds;
        this.recorded = recorded;
        this.method = method;
    }


    public Double getTotal() {
        return this.coreWattage.getAverage() + this.gpuWattage.getAverage() + this.otherWattage.getAverage() + this.ramWattage.getAverage();
    }

    public static class DoubleStatistics extends DoubleSummaryStatistics {

        private double sumOfSquare = 0.0d;
        private double sumOfSquareCompensation; // Low order bits of sum
        private double simpleSumOfSquare; // Used to compute right sum for
        // non-finite inputs

        @Override
        public void accept(double value) {
            super.accept(value);
            double squareValue = value * value;
            simpleSumOfSquare += squareValue;
            sumOfSquareWithCompensation(squareValue);
        }

        public DoubleStatistics combine(DoubleStatistics other) {
            super.combine(other);
            simpleSumOfSquare += other.simpleSumOfSquare;
            sumOfSquareWithCompensation(other.sumOfSquare);
            sumOfSquareWithCompensation(other.sumOfSquareCompensation);
            return this;
        }

        private void sumOfSquareWithCompensation(double value) {
            double tmp = value - sumOfSquareCompensation;
            double velvel = sumOfSquare + tmp; // Little wolf of rounding error
            sumOfSquareCompensation = (velvel - sumOfSquare) - tmp;
            sumOfSquare = velvel;
        }

        public double getSumOfSquare() {
            double tmp = sumOfSquare + sumOfSquareCompensation;
            if (Double.isNaN(tmp) && Double.isInfinite(simpleSumOfSquare)) {
                return simpleSumOfSquare;
            }
            return tmp;
        }

        public final double getStandardDeviation() {
            long count = getCount();
            double sumOfSquare = getSumOfSquare();
            double average = getAverage();
            return count > 0 ? Math.sqrt((sumOfSquare - count * Math.pow(average, 2)) / (count - 1)) : 0.0d;
        }

        public static Collector<Double, ?, DoubleStatistics> collector() {
            return Collector.of(DoubleStatistics::new, DoubleStatistics::accept, DoubleStatistics::combine);
        }

    }

}
