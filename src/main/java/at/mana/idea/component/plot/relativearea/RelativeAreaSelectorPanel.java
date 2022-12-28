package at.mana.idea.component.plot.relativearea;

import at.mana.idea.component.plot.FunctionTraceAxis;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.event.ActionListener;

public class RelativeAreaSelectorPanel extends JPanel {
    private final ComboBox<String> xSelector;
    private final ComboBox<String> ySelector;

    public RelativeAreaSelectorPanel () {
        JLabel xLabel = new JLabel("X-Axis:");
        xSelector = new ComboBox<>(new String[]{"Power", "Frequency", "Runtime"});
        xSelector.setSelectedIndex(0);
        this.add(xLabel);
        this.add(xSelector);

        JLabel yLabel = new JLabel("Y-Axis:");
        ySelector = new ComboBox<>(new String[]{"Power", "Frequency", "Runtime"});
        xSelector.setSelectedIndex(2);
        this.add(yLabel);
        this.add(ySelector);
    }

    public FunctionTraceAxis getXAxis () {
        if (xSelector.getSelectedIndex() == 0) {
            return FunctionTraceAxis.Power;
        } else if (xSelector.getSelectedIndex() == 1) {
            return FunctionTraceAxis.Frequency;
        } else {
            return FunctionTraceAxis.Runtime;
        }
    }

    public FunctionTraceAxis getYAxis () {
        if (ySelector.getSelectedIndex() == 0) {
            return FunctionTraceAxis.Power;
        } else if (ySelector.getSelectedIndex() == 1) {
            return FunctionTraceAxis.Frequency;
        } else {
            return FunctionTraceAxis.Runtime;
        }
    }

    public void addXSelectorListener (ActionListener listener) {
        this.xSelector.addActionListener(listener);
    }

    public void addYSelectorListener (ActionListener listener) {
        this.ySelector.addActionListener(listener);
    }
}
