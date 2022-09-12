package at.mana.idea.component.plot.relativearea;

import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;

public class RelativeAreaSelectorPanel extends JPanel {
    private final ComboBox<String> xSelector;
    private final ComboBox<String> ySelector;

    public RelativeAreaSelectorPanel () {
        JLabel xLabel = new JLabel("X-Axis:");
        xSelector = new ComboBox<>(new String[]{"Energy", "Frequency", "Runtime"});
        xSelector.setSelectedIndex(0);
        this.add(xLabel);
        this.add(xSelector);

        JLabel yLabel = new JLabel("Y-Axis:");
        ySelector = new ComboBox<>(new String[]{"Energy", "Frequency", "Runtime"});
        xSelector.setSelectedIndex(2);
        this.add(yLabel);
        this.add(ySelector);
    }

    public RelativeAreaAxis getXAxis () {
        if (xSelector.getSelectedIndex() == 0) {
            return RelativeAreaAxis.Energy;
        } else if (xSelector.getSelectedIndex() == 1) {
            return RelativeAreaAxis.Frequency;
        } else {
            return RelativeAreaAxis.Runtime;
        }
    }

    public RelativeAreaAxis getYAxis () {
        if (ySelector.getSelectedIndex() == 0) {
            return RelativeAreaAxis.Energy;
        } else if (ySelector.getSelectedIndex() == 1) {
            return RelativeAreaAxis.Frequency;
        } else {
            return RelativeAreaAxis.Runtime;
        }
    }
}
