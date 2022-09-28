package at.mana.idea.component.plot.relativearea;

public class RelativeArea {
    private final double energy;
    private final double frequency;
    private final double runtime;

    public RelativeArea(double energy, double frequency, double runtime) {
        this.energy = energy;
        this.frequency = frequency;
        this.runtime = runtime;
    }

    public double getAxisValue (RelativeAreaAxis axis) {
        if (axis == RelativeAreaAxis.Energy) {
            return this.energy;
        } else if (axis == RelativeAreaAxis.Frequency) {
            return this.frequency;
        } else {
            return this.runtime;
        }
    }

    public double getCombinedAxisValue (RelativeAreaAxis axis1, RelativeAreaAxis axis2) {
        double value1 = getAxisValue(axis1);
        double value2 = getAxisValue(axis2);

        return value1 * value2;
    }
}
