package at.mana.idea.component.plot;

public enum FunctionTraceAxis {
    AveragePower("W"),
    AverageRuntime("ms"),
    Frequency("");

    // Member to hold the name
    private String unit;

    // constructor to set the unit
    FunctionTraceAxis(String unit){ this.unit = unit; }

    // the toString with and without unit included
    public String toString(boolean includeUnit) {
        if (includeUnit && unit.length() > 0) {
            return this.toString()+" ["+this.unit+"]";
        } else {
            return this.toString();
        }
    }
}