package at.mana.idea.component.plot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

/**
 * @author David Aigner
 */
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

    // name of function, without path
    public String getName () {
        return this.name;
    }

    // path of function, without name
    public String getPath () {
        return this.path;
    }

    // name of full function path without function name
    // used for comparing two FunctionTraces
    public String getFullName () {
        return this.path+"."+this.name;
    }

    // returns one of the three axis values
    public double getAxisValue (FunctionTraceAxis axis) {
        if (axis == FunctionTraceAxis.Power) {
            return this.power;
        } else if (axis == FunctionTraceAxis.Frequency) {
            return this.frequency;
        } else {
            return this.runtime;
        }
    }

    // calculates the area between to axis for Relative Area Plot
    public double getCombinedAxisValue (FunctionTraceAxis axis1, FunctionTraceAxis axis2) {
        double value1 = getAxisValue(axis1);
        double value2 = getAxisValue(axis2);

        return value1 * value2;
    }

    // appends a subtrace --> FunctionTrace which got callled by this FunctionTrace
    public void appendSubtrace (FunctionTrace subtrace) {
        this.subtraces.add(subtrace);
    }

    // compares the full paths of the trace
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionTrace that = (FunctionTrace) o;
        return Objects.equals(this.getFullName(), that.getFullName());
    }

    // converts object to JSON, used for flamegraph visualization
    public String toJson () {
        // generate base string
        String result = String.format(Locale.US, "{" +
                "\"name\":\"%s\"," +
                "\"path\":\"%s\"," +
                "\"power\":%f," +
                "\"frequency\":%f," +
                "\"runtime\":%f",
                this.name, this.path, this.power, this.frequency, this.runtime);

        // append subtraces array to json string
        String subtraceString = ",\"subtraces\":[";
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

    public boolean isInPath (String path) {
        String[] otherParts = path.split("\\.");
        String[] thisParts = this.path.split("\\.");

        if (otherParts.length > thisParts.length) { return false;}
        for (int i = 0; i < otherParts.length; i++) {
            if (!otherParts[i].equals(thisParts[i])) {
                return false;
            }
        }

        return true;
    }
}
