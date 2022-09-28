package at.mana.idea.component.plot.relativearea;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.xml.crypto.dsig.Transform;
import java.awt.*;
import java.awt.geom.Line2D;

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
            double maxValue = 0;
            for (int i = 0; i < this.model.getRelativeAreaCount(); i++) {
                RelativeArea currArea = this.model.getRelativeAreaAtPosition(i);
                xSum += currArea.getAxisValue(xAxis);
                if (currArea.getAxisValue(yAxis) > yMax) {
                    yMax = currArea.getAxisValue(yAxis);
                }

                double currValue = currArea.getCombinedAxisValue(xAxis, yAxis);
                if (currValue > maxValue) {
                    maxValue = currValue;
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

            //drawArrow(g, (int)(totalWidth * 0.025), (int)(totalHeight * 0.975), (int)(totalWidth * 0.025), (int)(totalHeight * 0.025));
            //drawArrow(g, (int)(totalWidth * 0.025), (int)(totalHeight * 0.975), (int)(totalWidth * 0.975), (int)(totalHeight * 0.975));

            int currentX = (int)(totalWidth * 0.05);
            int bottomY = (int)(totalHeight * 0.95);
            for (int i = 0; i < this.model.getRelativeAreaCount(); i++) {
                RelativeArea currArea = this.model.getRelativeAreaAtPosition(i);
                double currWidth = currArea.getAxisValue(xAxis) * factor;
                double currHeight = currArea.getAxisValue(yAxis) * factor;
                double currValue = currArea.getCombinedAxisValue(xAxis, yAxis);

                Rectangle testRect = new Rectangle(currentX, (int)(bottomY - currHeight), (int)currWidth, (int)currHeight);
                graphics.setColor(getColorByScore(currValue / maxValue));
                graphics.fill(testRect);
                graphics.setColor(JBColor.BLACK);
                graphics.draw(testRect);

                currentX += (int)(currArea.getAxisValue(xAxis) * factor);
            }
        }
    }

    private Color getColorByScore (double score) {
        return Color.getHSBColor((float)((1.0 - score) / 3),0.8f, 0.7f);
        //return new Color((int) (255 * score), (int) (255 * (1.0 - score)), 0);
    }

    private void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }
}
