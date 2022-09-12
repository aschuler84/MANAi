package at.mana.idea.component.plot.relativearea;

import javax.swing.*;
import java.awt.*;

public class RelativeAreaPlotComponent extends JPanel {
    private RelativeAreaPlotModel model;

    public RelativeAreaPlotComponent () {
        // inserting test data, TODO: exchange it with real data
        this.model = new RelativeAreaPlotModel();
        this.model.appendRelativeArea(new RelativeArea(10, 10, 10));
        this.model.appendRelativeArea(new RelativeArea(5, 20, 10));
        this.model.appendRelativeArea(new RelativeArea(20, 5, 5));

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
