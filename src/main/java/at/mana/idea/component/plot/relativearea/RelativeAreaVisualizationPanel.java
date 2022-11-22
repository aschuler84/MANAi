package at.mana.idea.component.plot.relativearea;

import at.mana.idea.component.plot.FunctionTrace;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

import static at.mana.idea.util.ColorUtil.HEAT_MAP_COLORS_DEFAULT;

public class RelativeAreaVisualizationPanel extends JPanel {
    private RelativeAreaSelectorPanel selectorPanel;
    private RelativeAreaPlotModel model;

    public RelativeAreaVisualizationPanel(RelativeAreaSelectorPanel selectorPanel, RelativeAreaPlotModel model) {
        this.selectorPanel = selectorPanel;
        this.model = model;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (model == null || model.getFunctionTraceCount() == 0) {
            this.add(new JLabel("No data has been added to the visualization."));
        } else {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g.create();
            RelativeAreaAxis xAxis = selectorPanel.getXAxis();
            RelativeAreaAxis yAxis = selectorPanel.getYAxis();

            double xSum = 0;
            double yMax = 0;
            double maxValue = 0;
            double valueSum = 0;
            for (int i = 0; i < this.model.getFunctionTraceCount(); i++) {
                FunctionTrace currArea = this.model.getFunctionTraceAtPosition(i);
                xSum += currArea.getAxisValue(xAxis);
                if (currArea.getAxisValue(yAxis) > yMax) {
                    yMax = currArea.getAxisValue(yAxis);
                }

                double currValue = currArea.getCombinedAxisValue(xAxis, yAxis);
                valueSum += currValue;
                if (currValue > maxValue) {
                    maxValue = currValue;
                }
            }

            int totalWidth = super.getWidth();
            int totalHeight = super.getHeight();
            double xFactor = (totalWidth - 100) / xSum;
            double yFactor = (totalHeight - 100) / (yMax * xFactor);
            double factor = xFactor;
            if (yFactor < 1) { factor *= yFactor; }
            if (factor < 0) { factor = 0; }

            int currentX = 50;
            int highestY = 0;
            int bottomY = totalHeight - 50;
            for (int i = 0; i < this.model.getFunctionTraceCount(); i++) {
                FunctionTrace currArea = this.model.getFunctionTraceAtPosition(i);
                double currWidth = currArea.getAxisValue(xAxis) * factor;
                double currHeight = currArea.getAxisValue(yAxis) * factor;
                double currValue = currArea.getCombinedAxisValue(xAxis, yAxis);

                Rectangle rect = new Rectangle(currentX, (int)(bottomY - currHeight), (int)currWidth, (int)currHeight);
                graphics.setColor(getColorByScore(currValue / maxValue));
                graphics.fill(rect);
                graphics.setColor(JBColor.BLACK);
                graphics.draw(rect);
                labelRect(graphics, currentX, currArea.getName(), Math.round(1000 * currValue / valueSum)/10.0+"%");

                currentX += (int)(currArea.getAxisValue(xAxis) * factor);
                if ((int)currArea.getAxisValue(yAxis) == (int)yMax) {
                    highestY = (int)(bottomY - currHeight);
                }
            }

            drawAxisSystem(g, (int)xSum, (int)yMax, currentX, highestY);
        }
    }

    private Color getColorByScore (double score) {
        System.out.println(score+" --> "+Math.round(score * 5)+" --> "+(int)Math.round(score * 5));
        return HEAT_MAP_COLORS_DEFAULT[(int)Math.round(score * 5)];

        //return Color.getHSBColor((float)((1.0 - score) / 3),0.8f, 0.7f);
    }

    private void drawAxisSystem(Graphics g, int maxXValue, int maxYValue, int maxXPosition, int maxYPosition) {
        drawAxis(g, true, maxXValue, maxXPosition);
        drawAxis(g, false, maxYValue, maxYPosition);
    }

    private void drawAxis(Graphics g, boolean horizontal, int maxValue, int maxPosition) {
        int totalWidth = super.getWidth();
        int totalHeight = super.getHeight();

        if (horizontal) {
            // draw main axis line
            g.drawLine(25, totalHeight - 25, totalWidth - 25, totalHeight - 25);

            // draw maximum delimiter
            g.drawLine(maxPosition, totalHeight - 20, maxPosition, totalHeight - 30);
            g.drawString(""+maxValue, maxPosition + 5, totalHeight - 10);

            // draw half delimiter
            int halfPosition = 50 + (maxPosition - 50) / 2;
            g.drawLine(halfPosition, totalHeight - 20, halfPosition, totalHeight - 30);
            g.drawString(""+(maxValue / 2), halfPosition + 5, totalHeight - 10);

            // draw quarter delimiter
            int quarterPosition = 50 + (maxPosition - 50) / 4;
            g.drawLine(quarterPosition, totalHeight - 20, quarterPosition, totalHeight - 30);
            g.drawString(""+(maxValue / 4), quarterPosition + 5, totalHeight - 10);

            // draw three quarter delimiter
            int threeQuarterPosition = 50 + (maxPosition - 50) * 3 / 4;
            g.drawLine(threeQuarterPosition, totalHeight - 20, threeQuarterPosition, totalHeight - 30);
            g.drawString(""+(maxValue * 3 / 4), threeQuarterPosition + 5, totalHeight - 10);
        } else {
            // draw main axis line
            g.drawLine(25, 25, 25, totalHeight - 25);

            // draw maximum delimiter
            g.drawLine(20, maxPosition, 30, maxPosition);
            g.drawString(""+maxValue, 10, maxPosition - 5);

            // draw half delimiter
            int halfPosition = totalHeight - 50 - ((totalHeight - 100) - (maxPosition - 50)) / 2;
            g.drawLine(20, halfPosition, 30, halfPosition);
            g.drawString(""+(maxValue / 2), 10, halfPosition - 5);
        }
    }

    private void labelRect (Graphics g, int xPos, String name, String value) {
        int totalHeight = super.getHeight();

        g.setColor(Color.BLACK);
        g.drawString(name, xPos + 5, totalHeight - 80);
        g.drawString(value, xPos + 5, totalHeight - 65);
    }
}
