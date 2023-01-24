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
    private final double averagePower;
    private final double averageRuntime;
    private final double frequency;
    private final ArrayList<FunctionTrace> subtraces;

    public FunctionTrace(String name, String path, double averagePower, double averageRuntime, double frequency) {
        this.name = name;
        this.path = path;
        this.averagePower = averagePower;
        this.averageRuntime = averageRuntime;
        this.frequency = frequency;
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
        if (axis == FunctionTraceAxis.AveragePower) {
            return this.averagePower;
        } else if (axis == FunctionTraceAxis.AverageRuntime) {
            return this.averageRuntime;
        } else {
            return this.frequency;
        }
    }

    // calculates the area between two axes
    public double getCombinedAxisValue (FunctionTraceAxis axis1, FunctionTraceAxis axis2) {
        double value1 = getAxisValue(axis1);
        double value2 = getAxisValue(axis2);

        return value1 * value2;
    }

    // calculates the volume between all three axes
    public double getVolume () {
        double value1 = getAxisValue(FunctionTraceAxis.AveragePower);
        double value2 = getAxisValue(FunctionTraceAxis.AverageRuntime);
        double value3 = getAxisValue(FunctionTraceAxis.Frequency);

        return value1 * value2 * value3;
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
    public String toJson (boolean includeSubtraces) {
        // generate base string
        String result = String.format(Locale.US, "{" +
                "\"name\":\"%s\"," +
                "\"path\":\"%s\"," +
                "\"averagePower\":%f," +
                "\"averageRuntime\":%f," +
                "\"frequency\":%f",
                this.name, this.path, this.averagePower, this.averageRuntime, this.frequency);

        // append subtraces array to json string
        if (includeSubtraces) {
            String subtraceString = ",\"subtraces\":[";
            if (this.subtraces.size() > 0) {
                for (FunctionTrace subtrace : subtraces) {
                    subtraceString += subtrace.toJson(true) + ",";
                }

                subtraceString = subtraceString.substring(0, subtraceString.length()-1);
            }

            subtraceString += "]";
            result += subtraceString;
        }

        return result + "}";
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

    public static String arrayListToJson(ArrayList<FunctionTrace> traces) {
        String result = "[";

        for (FunctionTrace trace : traces) {
            result += trace.toJson(false) + ",";
        }

        if (traces.size() > 0) { result = result.substring(0, result.length() - 1); }
        return result + "]";
    }
}
