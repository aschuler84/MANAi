package at.mana.idea.component.plot.relativevolume;

import at.mana.idea.component.plot.FunctionTrace;

import java.util.ArrayList;

public class RelativeVolumePlotModel {
    private final ArrayList<FunctionTrace> functionTraces;

    public RelativeVolumePlotModel () {
        this.functionTraces = new ArrayList<>();
    }

    public RelativeVolumePlotModel (ArrayList<FunctionTrace> functionTraces) {
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
