package at.mana.idea.component.plot.relativearea;

import at.mana.idea.component.plot.FunctionTrace;
import at.mana.idea.component.plot.ProjectBrowserPanel;
import at.mana.idea.component.plot.FunctionTraceAxis;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RelativeAreaPlotComponent extends JPanel {
    private RelativeAreaPlotModel model;
    private final ProjectBrowserPanel projectBrowser;
    private final RelativeAreaSelectorPanel selectorPanel;
    private final RelativeAreaVisualizationPanel visualizationPanel;

    public RelativeAreaPlotComponent () {
        // inserting test data, TODO: exchange it with real data
        this.model = new RelativeAreaPlotModel();
        FunctionTrace parent = new FunctionTrace("refresh", "example.project.GUI", 1, 1, 1);

        FunctionTrace child1 = new FunctionTrace("authorize", "com.example.project.logic", 1, 8, 3, parent);
        FunctionTrace child2 = new FunctionTrace("loadPersonData", "com.example.project.logic", 2, 1, 2, parent);
        FunctionTrace child3 = new FunctionTrace("loadLocationData", "com.example.project.logic", 14, 16, 1, parent);

        FunctionTrace child11 = new FunctionTrace("login", "com.example.project.auth", 3, 5, 1, child1);
        FunctionTrace child12 = new FunctionTrace("getToken", "com.example.project.auth", 5, 3, 1, child1);

        FunctionTrace child21 = new FunctionTrace("readPersonFromDB", "com.example.project.model", 5, 7, 1, child2);
        FunctionTrace child22 = new FunctionTrace("processPersonData", "com.example.project.model", 7, 4, 2, child2);

        FunctionTrace child31 = new FunctionTrace("readLocationFromDB", "com.example.project.model", 3, 5, 2, child3);
        FunctionTrace child32 = new FunctionTrace("processLocationData", "com.example.project.model", 6, 3, 3, child3);


        this.model.appendFunctionTrace(parent);
        this.model.appendFunctionTrace(child1);
        this.model.appendFunctionTrace(child11);
        this.model.appendFunctionTrace(child12);
        this.model.appendFunctionTrace(child2);
        this.model.appendFunctionTrace(child21);
        this.model.appendFunctionTrace(child22);
        this.model.appendFunctionTrace(child3);
        this.model.appendFunctionTrace(child31);
        this.model.appendFunctionTrace(child32);

        this.setLayout(new GridBagLayout());

        projectBrowser = new ProjectBrowserPanel();
        projectBrowser.setBackground(new JBColor(Gray._160, Gray._92));
        GridBagConstraints browserConstraints = new GridBagConstraints();
        browserConstraints.gridx = 0;
        browserConstraints.gridy = 0;
        browserConstraints.weightx = 0.2;
        browserConstraints.weighty = 1;
        browserConstraints.gridheight = 2;
        browserConstraints.anchor = GridBagConstraints.NORTHWEST;
        browserConstraints.fill = GridBagConstraints.BOTH;
        this.add(projectBrowser, browserConstraints);

        selectorPanel = new RelativeAreaSelectorPanel();
        selectorPanel.setBackground(new JBColor(Gray._160, Gray._92));
        GridBagConstraints selectorConstraints = new GridBagConstraints();
        selectorConstraints.gridx = 1;
        selectorConstraints.gridy = 0;
        selectorConstraints.weightx = 0.8;
        selectorConstraints.weighty = 0.1;
        selectorConstraints.anchor = GridBagConstraints.NORTHWEST;
        selectorConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(selectorPanel, selectorConstraints);

        visualizationPanel = new RelativeAreaVisualizationPanel();
        visualizationPanel.setBackground(new JBColor(Gray._208, Gray._48));
        GridBagConstraints visualizationConstraints = new GridBagConstraints();
        visualizationConstraints.gridx = 1;
        visualizationConstraints.gridy = 1;
        visualizationConstraints.weightx = 0.8;
        visualizationConstraints.weighty = 0.9;
        visualizationConstraints.anchor = GridBagConstraints.NORTHWEST;
        visualizationConstraints.fill = GridBagConstraints.BOTH;
        this.add(visualizationPanel, visualizationConstraints);

        projectBrowser.addSelectionChangedListener(e -> { this.refreshPlot(); });
        selectorPanel.addXSelectorListener(e -> { this.refreshPlot(); });
        selectorPanel.addYSelectorListener(e -> { this.refreshPlot(); });

        this.refreshPlot();
    }

    public void setModel(RelativeAreaPlotModel model) {
        this.model = model;
        this.refreshPlot();
    }

    // uses data from project browser and selector panel to refresh visualization panel
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

        // determine the selected resourrce for both axes
        FunctionTraceAxis xAxis = this.selectorPanel.getXAxis();
        FunctionTraceAxis yAxis = this.selectorPanel.getYAxis();

        // refresh the visualization panel
        this.visualizationPanel.refresh(tracesToBeDisplayed, xAxis, yAxis);
    }
}
