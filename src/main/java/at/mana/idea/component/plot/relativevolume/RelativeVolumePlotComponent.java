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
        this.model.appendFunctionTrace(new FunctionTrace("Optimize", "TestProject.pack1.Optimizer", 16, 6, 8));
        this.model.appendFunctionTrace(new FunctionTrace("Summarize", "TestProject.pack2.Summarizer", 7, 3, 11));
        this.model.appendFunctionTrace(new FunctionTrace("Evaluate", "TestProject.pack3.Evaluator", 6, 3, 9));
        this.model.appendFunctionTrace(new FunctionTrace("Schedule", "TestProject.pack4.Scheduler", 4, 9, 7));

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

    // uses data from project browser to refresh visualization panel
    private void refreshPlot () {
        ArrayList<FunctionTrace> tracesToBeDisplayed = new ArrayList<>();
        String filterPath = this.projectBrowser.getSelectedClass();

        // filter all function traces in model by selected project browser path
        for (int i = 0; i < this.model.getFunctionTraceCount(); i++) {
            FunctionTrace currTrace = this.model.getFunctionTraceAtPosition(i);

            if (filterPath == null || filterPath.length() == 0 || currTrace.isInPath(filterPath)) {
                tracesToBeDisplayed.add(currTrace);
            }
        }

        // sort function traces by their volume
        tracesToBeDisplayed.sort((o1, o2) -> Double.compare(o2.getVolume(), o1.getVolume()));

        // refresh the visualization panel
        String jsonData = FunctionTrace.arrayListToJson(tracesToBeDisplayed);
        this.visualizationPanel.refresh(jsonData);
    }
}
