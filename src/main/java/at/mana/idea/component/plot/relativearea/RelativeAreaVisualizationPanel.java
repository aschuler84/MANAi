package at.mana.idea.component.plot.relativearea;

import at.mana.idea.component.plot.FunctionTrace;
import at.mana.idea.component.plot.FunctionTraceAxis;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static at.mana.idea.util.ColorUtil.HEAT_MAP_COLORS_DEFAULT;

public class RelativeAreaVisualizationPanel extends JPanel {
    private final JLabel noDataLabel;
    private ArrayList<FunctionTrace> traces;
    private FunctionTraceAxis xAxis;
    private FunctionTraceAxis yAxis;

    public RelativeAreaVisualizationPanel() {
        this.traces = new ArrayList<>();
        noDataLabel = new JLabel("No data has been added to the visualization.");
    }

    public void refresh(ArrayList<FunctionTrace> traces, FunctionTraceAxis xAxis, FunctionTraceAxis yAxis) {
        this.traces = traces;
        this.xAxis = xAxis;
        this.yAxis = yAxis;

        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.remove(noDataLabel);

        if (this.traces.size() == 0) {
            this.add(noDataLabel);
            this.revalidate();
        } else {
            Graphics2D graphics = (Graphics2D)g.create();

            double xSum = 0;
            double yMax = 0;
            double maxValue = 0;
            double valueSum = 0;
            for (FunctionTrace currTrace : this.traces) {
                xSum += currTrace.getAxisValue(this.xAxis);
                if (currTrace.getAxisValue(this.yAxis) > yMax) {
                    yMax = currTrace.getAxisValue(this.yAxis);
                }

                double currValue = currTrace.getCombinedAxisValue(this.xAxis, this.yAxis);
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
            for (FunctionTrace currTrace : this.traces) {
                double currWidth = currTrace.getAxisValue(this.xAxis) * factor;
                double currHeight = currTrace.getAxisValue(this.yAxis) * factor;
                double currValue = currTrace.getCombinedAxisValue(this.xAxis, this.yAxis);

                drawArea(graphics, currentX, bottomY, (int)Math.round(currWidth), (int)Math.round(currHeight), getColorByScore(currValue / maxValue));
                labelArea(graphics, currentX, currTrace.getName(), Math.round(1000 * currValue / valueSum) / 10.0 + "%");

                currentX += (int) (currTrace.getAxisValue(this.xAxis) * factor);
                if ((int) currTrace.getAxisValue(this.yAxis) == (int) yMax) {
                    highestY = (int) (bottomY - currHeight);
                }
            }

            drawAxisSystem(g, (int)xSum, (int)yMax, currentX, highestY);
        }
    }

    private void drawArea (Graphics g, int posX, int posY, int width, int height, Color c) {
        Rectangle rect = new Rectangle(posX, posY - height, width, height);
        g.setColor(c);
        g.fillRect(posX, posY-height, width, height);
        g.setColor(JBColor.BLACK);
        g.drawRect(posX, posY-height, width, height);
    }

    private void labelArea (Graphics g, int xPos, String name, String value) {
        int totalHeight = super.getHeight();

        g.setColor(JBColor.WHITE);
        g.drawString(name, xPos + 5, totalHeight - 80);
        g.drawString(value, xPos + 5, totalHeight - 65);
    }

    private Color getColorByScore (double score) {
        return HEAT_MAP_COLORS_DEFAULT[(int)Math.round(score * 5)];
        //return Color.getHSBColor((float)((1.0 - score) / 3),0.8f, 0.7f);
    }

    private void drawAxisSystem(Graphics g, int maxXValue, int maxYValue, int maxXPosition, int maxYPosition) {
        g.setColor(JBColor.DARK_GRAY);
        drawHorizontalAxis(g, maxXValue, maxXPosition);
        drawVerticalAxis(g, maxYValue, maxYPosition);
    }

    private void drawHorizontalAxis(Graphics g, double maxValue, int maxPosition) {
        int totalWidth = super.getWidth();
        int totalHeight = super.getHeight();

        // draw main axis line
        g.drawLine(25, totalHeight - 25, totalWidth - 25, totalHeight - 25);

        // draw maximum delimiter
        drawDelimiter(g, true, maxPosition, totalHeight - 25, maxValue);

        // draw half delimiter
        int halfPosition = 50 + (maxPosition - 50) / 2;
        drawDelimiter(g, true, halfPosition, totalHeight - 25, maxValue / 2);

        // draw quarter delimiter
        int quarterPosition = 50 + (maxPosition - 50) / 4;
        drawDelimiter(g, true, quarterPosition, totalHeight - 25, maxValue / 4);

        // draw three quarter delimiter
        int threeQuarterPosition = 50 + (maxPosition - 50) * 3 / 4;
        drawDelimiter(g, true, threeQuarterPosition, totalHeight - 25, maxValue * 3 / 4);

        // draw axis label
        g.drawString(xAxis.toString(true), totalWidth - 30 - g.getFontMetrics().stringWidth(xAxis.toString(true)), totalHeight - 10);
    }

    private void drawVerticalAxis(Graphics g, double maxValue, int maxPosition) {
        int totalHeight = super.getHeight();

        // draw main axis line
        g.drawLine(25, 25, 25, totalHeight - 25);

        // draw maximum delimiter
        drawDelimiter(g, false, 25, maxPosition, maxValue);

        // draw half delimiter
        int halfPosition = totalHeight - 50 - ((totalHeight - 100) - (maxPosition - 50)) / 2;
        drawDelimiter(g, false, 25, halfPosition, maxValue / 2);

        // draw axis label
        g.drawString(yAxis.toString(true), 5, 15);

    }

    private void drawDelimiter (Graphics g, boolean horizontal, int xPos, int yPos, double value ) {
        if (horizontal) {
            // draw horizontal delimiter
            g.drawLine(xPos, yPos - 5, xPos, yPos + 5);
            g.drawString(""+value, xPos + 5, yPos - 10);
        } else {
            // draw vertical delimiter
            g.drawLine(xPos - 5, yPos, xPos + 5, yPos);
            g.drawString(""+value, xPos - 20, yPos - 5);
        }
    }
}
