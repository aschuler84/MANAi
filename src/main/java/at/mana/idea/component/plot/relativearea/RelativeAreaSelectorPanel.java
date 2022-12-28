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
        xSelector = new ComboBox<>(new String[]{"Average Power", "Average Runtime", "Frequency"});
        xSelector.setSelectedIndex(0);
        this.add(xLabel);
        this.add(xSelector);

        JLabel yLabel = new JLabel("Y-Axis:");
        ySelector = new ComboBox<>(new String[]{"Average Power", "Average Runtime", "Frequency"});
        xSelector.setSelectedIndex(2);
        this.add(yLabel);
        this.add(ySelector);
    }

    public FunctionTraceAxis getXAxis () {
        if (xSelector.getSelectedIndex() == 0) {
            return FunctionTraceAxis.AveragePower;
        } else if (xSelector.getSelectedIndex() == 1) {
            return FunctionTraceAxis.AverageRuntime;
        } else {
            return FunctionTraceAxis.Frequency;
        }
    }

    public FunctionTraceAxis getYAxis () {
        if (ySelector.getSelectedIndex() == 0) {
            return FunctionTraceAxis.AveragePower;
        } else if (ySelector.getSelectedIndex() == 1) {
            return FunctionTraceAxis.AverageRuntime;
        } else {
            return FunctionTraceAxis.Frequency;
        }
    }

    public void addXSelectorListener (ActionListener listener) {
        this.xSelector.addActionListener(listener);
    }

    public void addYSelectorListener (ActionListener listener) {
        this.ySelector.addActionListener(listener);
    }
}
