package at.mana.idea.component.plot.relativearea;

import at.mana.idea.component.plot.FunctionTrace;

import javax.swing.*;
import java.awt.*;

public class RelativeAreaPlotComponent extends JPanel {
    private RelativeAreaPlotModel model;

    public RelativeAreaPlotComponent () {
        // inserting test data, TODO: exchange it with real data
        this.model = new RelativeAreaPlotModel();
        this.model.appendFunctionTrace(new FunctionTrace("Func-1", "ExampleProject.ExampleClass", 10, 10, 10));
        this.model.appendFunctionTrace(new FunctionTrace("Func-2", "ExampleProject.ExampleClass", 5, 20, 10));
        this.model.appendFunctionTrace(new FunctionTrace("Func-3", "ExampleProject.ExampleClass", 19, 5, 5));
        this.model.appendFunctionTrace(new FunctionTrace("Func-4", "ExampleProject.ExampleClass", 5, 5, 5));
        this.model.appendFunctionTrace(new FunctionTrace("Func-5", "ExampleProject.ExampleClass", 20, 10, 5));
        this.model.appendFunctionTrace(new FunctionTrace("Func-6", "ExampleProject.ExampleClass", 15, 5, 7));
        this.model.appendFunctionTrace(new FunctionTrace("Func-7", "ExampleProject.ExampleClass", 21, 17, 23));
        this.model.appendFunctionTrace(new FunctionTrace("Func-8", "ExampleProject.ExampleClass", 20, 5, 41));
        this.model.appendFunctionTrace(new FunctionTrace("Func-9", "ExampleProject.ExampleClass", 38, 21, 5));
        this.model.appendFunctionTrace(new FunctionTrace("Func-10", "ExampleProject.ExampleClass", 15, 14, 15));
        this.model.appendFunctionTrace(new FunctionTrace("Func-11", "ExampleProject.ExampleClass", 11, 19, 20));
        this.model.appendFunctionTrace(new FunctionTrace("Func-12", "ExampleProject.ExampleClass", 28, 5, 3));

        this.setLayout(new GridBagLayout());

        RelativeAreaSelectorPanel selectorPanel = new RelativeAreaSelectorPanel();
        GridBagConstraints selectorConstraints = new GridBagConstraints();
        selectorConstraints.gridx = 0;
        selectorConstraints.gridy = 0;
        selectorConstraints.weightx = 1;
        selectorConstraints.weighty = 0.1;
        selectorConstraints.anchor = GridBagConstraints.NORTHWEST;
        this.add(selectorPanel, selectorConstraints);

        RelativeAreaVisualizationPanel visualizationPanel = new RelativeAreaVisualizationPanel(selectorPanel, this.model);
        GridBagConstraints visualizationConstraints = new GridBagConstraints();
        visualizationConstraints.gridx = 0;
        visualizationConstraints.gridy = 1;
        visualizationConstraints.weightx = 1;
        visualizationConstraints.weighty = 0.9;
        visualizationConstraints.anchor = GridBagConstraints.NORTHWEST;
        visualizationConstraints.fill = GridBagConstraints.BOTH;
        this.add(visualizationPanel, visualizationConstraints);
    }

    public void setModel(RelativeAreaPlotModel model) {
        this.model = model;
        this.paintImmediately(getLocation().x, getLocation().y, getWidth(),getHeight());
    }
}
