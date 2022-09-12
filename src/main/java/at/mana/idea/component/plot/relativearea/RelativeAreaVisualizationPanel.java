package at.mana.idea.component.plot.relativearea;

import javax.swing.*;
import java.awt.*;

public class RelativeAreaVisualizationPanel extends JPanel {
    private RelativeAreaSelectorPanel selectorPanel;
    private RelativeAreaPlotModel model;

    public RelativeAreaVisualizationPanel(RelativeAreaSelectorPanel selectorPanel, RelativeAreaPlotModel model) {
        this.selectorPanel = selectorPanel;
        this.model = model;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (model == null || model.getRelativeAreaCount() == 0) {
            this.add(new JLabel("No data has been added to the visualization."));
        } else {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g.create();
            RelativeAreaAxis xAxis = selectorPanel.getXAxis();
            RelativeAreaAxis yAxis = selectorPanel.getYAxis();

            double xSum = 0;
            double yMax = 0;
            for (int i = 0; i < this.model.getRelativeAreaCount(); i++) {
                RelativeArea currArea = this.model.getRelativeAreaAtPosition(i);
                xSum += currArea.getAxisValue(xAxis);
                if (currArea.getAxisValue(yAxis) > yMax) {
                    yMax = currArea.getAxisValue(yAxis);
                }
            }

            int totalWidth = super.getWidth();
            int totalHeight = super.getHeight();
            double xFactor = (totalWidth * 0.9) / xSum;
            double yFactor = (totalHeight * 0.9) / (yMax * xFactor);
            double factor = xFactor;
            if (yFactor < 1) {
                factor *= yFactor;
            }

            int currentX = (int)(totalWidth * 0.05);
            for (int i = 0; i < this.model.getRelativeAreaCount(); i++) {
                RelativeArea currArea = this.model.getRelativeAreaAtPosition(i);

                Rectangle testRect = new Rectangle(currentX, (int)(totalHeight * 0.95 - currArea.getAxisValue(yAxis) * factor), (int)(currArea.getAxisValue(xAxis) * factor), (int)(currArea.getAxisValue(yAxis) * factor));
                graphics.draw(testRect);

                currentX += (int)(currArea.getAxisValue(xAxis) * factor);
            }
        }
    }
}
