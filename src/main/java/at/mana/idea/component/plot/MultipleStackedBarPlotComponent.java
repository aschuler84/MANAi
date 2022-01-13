/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.plot;

import at.mana.core.util.NumberScale;
import at.mana.idea.util.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class MultipleStackedBarPlotComponent extends JPanel {

    private Insets insets = JBUI.insets(20);
    private SingleStackedBarPlotModel[] model;
    private final Color[] colors = ColorUtil.HEAT_MAP_COLORS_DEFAULT;

    public MultipleStackedBarPlotComponent() {
        this.setOpaque( true );
    }

    public void setModel(SingleStackedBarPlotModel[] model) {
        this.model = model;
        this.invalidate();
        this.validate();
        this.repaint();
    }

    private static final int GAP = 30;

    // Split in 2 areas - first is for methods
    // if text of labels is too long -> shorten with ...
    // chart resizes in X coordinate
    // chart does not resize in y coordinate???
    // if available space is lower than min height - use min height
    // otherwise use available space
    // min height is computed based on current height


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
        int leftWidth = (int) (drawingWidth * 0.9);  // omit floating point precision
        int leftHeight = drawingHeight;
        int rightWidth  = endX - leftWidth;
        int rightHeight = drawingHeight;
        int startXRight = startX + leftWidth + GAP;

        if( this.model != null && this.model.length != 0 ) {
            //graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int legendHeight = 20;
            int startRX = rightWidth;
            int startRY = startY;
            int endRX = drawingWidth;
            int endRY = startRY + rightHeight - legendHeight;

            // from > to range
            // NumberScale.domain(  )

            graphics.setColor(getBackground());
            Line2D topLine = new Line2D.Double( startRX, startRY, endRX, startRY );
            Line2D bottomLine = new Line2D.Double( startRX, endRY, endRX, endRY);
            Line2D leftLine = new Line2D.Double( startRX, startRY, startRX, endRY );
            graphics.setColor( Color.GRAY );
            graphics.setStroke( new BasicStroke(0.8f) );
            //graphics.draw( topLine );
            graphics.draw( leftLine );
            graphics.draw( bottomLine );

            // get height from text
            int fontHeight = graphics.getFontMetrics().getHeight();
            // available height -  starts from startRy + 10 till endRy - 1
            var chartHeight = endRY - startRY + 10;
            var barHeight = chartHeight / model.length;
            var startYTick = startRY + 9 + barHeight/2;
            var endYTick = endRY - 1 - barHeight/2;

            var fn = NumberScale.domain( 0.0, (double) model.length - 1 ).range( startYTick, endYTick );

            for( int i = 0; i < model.length; i++ ) {
                var yPos = fn.apply(  i );
                g.drawString( model[i].getTitle(),
                        startRX - 12 - graphics.getFontMetrics().stringWidth(model[i].getTitle()),
                        yPos - graphics.getFontMetrics().getHeight() / 2 );
                Line2D tickLine = new Line2D.Double( startRX - 6, yPos, startRX, yPos );
                graphics.draw( tickLine );
            }


            /* for( int i = 0; i < model.getNoOfStacks(); i++ ) {
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
                graphics.draw( bottomLine ); */
            //}
        } else {
            // TODO: print empty text
        }
        graphics.dispose();
    }

}
