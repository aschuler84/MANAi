/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.plot;

import at.mana.idea.util.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Arrays;

public class SingleStackedBarPlotComponent extends JPanel {

    private Insets insets = JBUI.insets(20);
    private SingleStackedBarPlotModel model;
    private final Color[] colors = ColorUtil.STACK_COLORS_DEFAULT;

    public SingleStackedBarPlotComponent() {
        this.setOpaque( true );
    }

    public void setModel(SingleStackedBarPlotModel model) {
        this.model = model;
        this.invalidate();
        this.validate();
        this.repaint();
    }

    private static final int GAP = 30;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g.create();
        int startY = insets.top;
        int startX = insets.left;
        int endX = this.getWidth() - insets.right;
        int endY = this.getHeight() - insets.bottom;
        int drawingWidth = endX - startX;
        int drawingHeight = endY - startY;
        // next divide drawing content in 2 areas
        int leftWidth = (int) (drawingWidth * 0.2);  // omit floating point precision
        int leftHeight = drawingHeight;
        int rightWidth  = endX - leftWidth;
        int startXRight = startX + leftWidth + GAP;
        if( this.model != null ) {
            //graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getBackground());
            int startPosY = endY;
            int startPosYLeft = startY + graphics.getFontMetrics().getHeight() + 10;
            Line2D bottomLine = new Line2D.Double( startX-6, startPosY, startX + leftWidth +6, startPosY);
            Line2D topLine = new Line2D.Double( startX-6, startY, startX + leftWidth +6, startY);
            for( int i = 0; i < model.getNoOfStacks(); i++ ) {
                // draw each block as a fraction of the available space
                double ratio = model.getValueFor( i ) / model.getTotalValue();
                int stackHeight = (int) Double.max(1,leftHeight * ratio);
                graphics.setColor( colors[ i % colors.length ] );
                if( i == model.getNoOfStacks() - 1 ) {
                    stackHeight = Integer.max(startPosY - startY, stackHeight);
                    graphics.fillRect(startX, startPosY - stackHeight, leftWidth, stackHeight);
                } else {
                    graphics.fillRect(startX, startPosY - stackHeight, leftWidth, stackHeight);
                }
                // startPosY draw a line to respective text
                graphics.setColor( JBColor.foreground() );
                String legend = model.getLegendFor( i);
                graphics.drawString( legend, 6 + startXRight, startPosYLeft + ( ( ( model.getNoOfStacks() - 1) - i ) * (graphics.getFontMetrics().getHeight() + 10) ) );
                int stringWidth = graphics.getFontMetrics().stringWidth( legend );
                graphics.setColor( colors[ i % colors.length ] );
                graphics.setStroke( new BasicStroke(0.8f,
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_ROUND,
                        1.0f,
                        new float[]{ 2f, 0f, 2f },
                        2f) );
                graphics.drawLine(  startXRight, 3 + startPosYLeft + ( ( ( model.getNoOfStacks() - 1) - i ) * (graphics.getFontMetrics().getHeight() + 10) ), startXRight+ stringWidth, 3 + startPosYLeft + ( ( ( model.getNoOfStacks() - 1) - i ) * (graphics.getFontMetrics().getHeight() + 10) ) );
                graphics.drawLine( startX + leftWidth, startPosY, startXRight, 3 + startPosYLeft + ( ( ( model.getNoOfStacks() - 1) - i ) * (graphics.getFontMetrics().getHeight() + 10) ) );
                startPosY = startPosY - stackHeight;
                //startPosYLeft = startPosYLeft + graphics.getFontMetrics().getHeight() + 10;
                graphics.setColor( Color.GRAY );
                graphics.setStroke( new BasicStroke(0.8f) );
                graphics.draw( topLine );
                graphics.draw( bottomLine );
            }
        } else {
            // TODO: print empty text
        }
        graphics.dispose();
    }

}
