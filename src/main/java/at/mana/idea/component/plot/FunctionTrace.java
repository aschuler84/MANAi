package at.mana.idea.component.plot;

import at.mana.idea.component.plot.relativearea.RelativeAreaAxis;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FunctionTrace {
    private final String name;
    private final String path;
    private final double power;
    private final double frequency;
    private final double runtime;
    private final ArrayList<FunctionTrace> subtraces;

    public FunctionTrace(String name, String path, double power, double frequency, double runtime) {
        this.name = name;
        this.path = path;
        this.power = power;
        this.frequency = frequency;
        this.runtime = runtime;
        this.subtraces = new ArrayList<>();
    }

    public String getName () {
        return this.name;
    }

    // Used for comparing two FunctionTraces
    public String getFullName () {
        return this.path+"."+this.name;
    }

    public double getAxisValue (RelativeAreaAxis axis) {
        if (axis == RelativeAreaAxis.Power) {
            return this.power;
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

    public void appendSubtrace (FunctionTrace subtrace) {
        this.subtraces.add(subtrace);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionTrace that = (FunctionTrace) o;
        return Objects.equals(this.getFullName(), that.getFullName());
    }

    public String toJson () {
        String result = String.format(Locale.US, "{" +
                "name:'%s'," +
                "path:'%s'," +
                "power:%f," +
                "frequency:%f," +
                "runtime:%f", this.name, this.path, this.power, this.frequency, this.runtime);

        String subtraceString = ",subtraces:[";
        if (this.subtraces.size() > 0) {
            for (FunctionTrace subtrace : subtraces) {
                subtraceString += subtrace.toJson() + ",";
            }

            subtraceString = subtraceString.substring(0, subtraceString.length()-1);
            subtraceString += "]";
        } else {
            subtraceString = "";
        }


        return result + subtraceString + "}";
    }
}
