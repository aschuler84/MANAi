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
        /*this.model.appendFunctionTrace(new FunctionTrace("Func-1", "ExampleProject.ExampleClass", 10, 10, 10));
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
        this.model.appendFunctionTrace(new FunctionTrace("Func-12", "ExampleProject.ExampleClass", 28, 5, 3));*/
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

    private void refreshPlot () {
        ArrayList<FunctionTrace> tracesToBeDisplayed = new ArrayList<>();
        String filterPath = this.projectBrowser.getSelectedClass();

        for (int i = 0; i < this.model.getFunctionTraceCount(); i++) {
            FunctionTrace currTrace = this.model.getFunctionTraceAtPosition(i);

            if (filterPath == null || filterPath.length() == 0 || currTrace.isInPath(filterPath)) {
                tracesToBeDisplayed.add(currTrace);
            }
        }

        FunctionTraceAxis xAxis = this.selectorPanel.getXAxis();
        FunctionTraceAxis yAxis = this.selectorPanel.getYAxis();
        this.visualizationPanel.refresh(tracesToBeDisplayed, xAxis, yAxis);
    }
}
