package at.mana.idea.component.plot.relativearea;

import at.mana.idea.component.plot.FunctionTrace;

import java.util.ArrayList;

public class RelativeAreaPlotModel {
    private final ArrayList<FunctionTrace> functionTraces;

    public RelativeAreaPlotModel () {
        this.functionTraces = new ArrayList<>();
    }

    public RelativeAreaPlotModel (ArrayList<FunctionTrace> functionTraces) {
        this.functionTraces = functionTraces;
    }

    public void appendFunctionTrace(FunctionTrace functionTrace) {
        this.functionTraces.add(functionTrace);
    }

    public FunctionTrace getFunctionTraceAtPosition(int index) {
        if (index >= 0 && index < functionTraces.size()) {
            return this.functionTraces.get(index);
        } else {
            throw new IndexOutOfBoundsException("Provided Index was out of bounds!");
        }
    }

    public int getFunctionTraceCount() {
        return this.functionTraces.size();
    }
}
