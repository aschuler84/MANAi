package at.mana.idea.component.plot.relativevolume;

import at.mana.idea.component.plot.FunctionTrace;
import at.mana.idea.component.plot.ProjectBrowserPanel;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RelativeVolumePlotComponent extends JPanel {
    private RelativeVolumePlotModel model;
    private final ProjectBrowserPanel projectBrowser;
    private final RelativeVolumeVisualizationPanel visualizationPanel;

    public RelativeVolumePlotComponent () {
        // inserting test data, TODO: exchange it with real data
        this.model = new RelativeVolumePlotModel();
        this.model.appendFunctionTrace(new FunctionTrace("Bark", "TestProject.pack1.Dog", 3, 6, 4));
        this.model.appendFunctionTrace(new FunctionTrace("Walk", "TestProject.pack1.Dog", 8, 3, 2));
        this.model.appendFunctionTrace(new FunctionTrace("Miau", "TestProject.pack2.Cat", 1, 2, 2));
        this.model.appendFunctionTrace(new FunctionTrace("Stray", "TestProject.pack2.Cat", 1, 8, 6));

        this.setLayout(new GridBagLayout());

        projectBrowser = new ProjectBrowserPanel();
        projectBrowser.setBackground(new JBColor(Gray._160, Gray._92));
        GridBagConstraints browserConstraints = new GridBagConstraints();
        browserConstraints.gridx = 0;
        browserConstraints.gridy = 0;
        browserConstraints.weightx = 0.2;
        browserConstraints.weighty = 1;
        browserConstraints.anchor = GridBagConstraints.NORTHWEST;
        browserConstraints.fill = GridBagConstraints.BOTH;
        this.add(projectBrowser, browserConstraints);

        visualizationPanel = new RelativeVolumeVisualizationPanel();
        visualizationPanel.setBackground(new JBColor(Gray._208, Gray._48));
        GridBagConstraints visualizationConstraints = new GridBagConstraints();
        visualizationConstraints.gridx = 1;
        visualizationConstraints.gridy = 0;
        visualizationConstraints.weightx = 0.8;
        visualizationConstraints.weighty = 1;
        visualizationConstraints.anchor = GridBagConstraints.NORTHWEST;
        visualizationConstraints.fill = GridBagConstraints.BOTH;
        this.add(visualizationPanel, visualizationConstraints);

        projectBrowser.addSelectionChangedListener(e -> { this.refreshPlot(); });

        this.refreshPlot();
    }

    public void setModel(RelativeVolumePlotModel model) {
        this.model = model;
        this.refreshPlot();
    }

    private void refreshPlot () {
        ArrayList<FunctionTrace> tracesToBeDisplayed = new ArrayList<>();
        String filterPath = this.projectBrowser.getSelectedClass();

        for (int i = 0; i < this.model.getFunctionTraceCount(); i++) {
            FunctionTrace currTrace = this.model.getFunctionTraceAtPosition(i);

            if (filterPath == null || filterPath.length() == 0 || currTrace.isInPath(filterPath)) {
                tracesToBeDisplayed.add(currTrace);
            }
        }

        tracesToBeDisplayed.sort((o1, o2) -> {
            if (o1.getVolume() < o2.getVolume()) { return 1; }
            else if (o1.getVolume() > o2.getVolume()) { return -1; }
            else { return 0; }
        });

        this.visualizationPanel.refresh(FunctionTrace.arrayListToJson(tracesToBeDisplayed));
    }
}
