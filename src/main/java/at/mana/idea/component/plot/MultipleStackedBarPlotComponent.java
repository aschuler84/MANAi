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
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.stream.IntStream;

public class MultipleStackedBarPlotComponent extends JPanel {

    private Insets insets = JBUI.insets(6);
    private MultipleStackedBarPlotModel model;
    private final Color[] colors = ColorUtil.HEAT_MAP_COLORS_DEFAULT;

    public MultipleStackedBarPlotComponent() {
        this.setOpaque( true );
    }

    public void setModel(MultipleStackedBarPlotModel model) {
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
        int leftWidth = (int) (drawingWidth * 0.8);  // omit floating point precision
        int leftHeight = drawingHeight;
        int rightWidth  = endX - leftWidth;
        int rightHeight = drawingHeight;
        int startXRight = startX + leftWidth + GAP;

        if( this.model != null && this.model.getSeries().length != 0 ) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int axisHeight = 20;
            int legendHeight = 20;
            int startRX = rightWidth;
            int startRY = startY + axisHeight;
            int endRX = drawingWidth;
            int endRY = startRY + rightHeight - ( legendHeight +  axisHeight);

            graphics.setColor(getBackground());
            Line2D topLine = new Line2D.Double( startRX, startRY, endRX, startRY );
            Line2D bottomLine = new Line2D.Double( startRX, endRY, endRX-100, endRY);
            Line2D leftLine = new Line2D.Double( startRX, startRY, startRX, endRY );
            graphics.setColor( Color.GRAY );
            graphics.setStroke( new BasicStroke(0.80f) );  // determine ratio based on largest string
            //graphics.draw( topLine );
            graphics.draw( leftLine );
            graphics.draw( bottomLine );



            var chartHeight = endRY - startRY + 10;
            var chartWidth = endRX - startRX - 100;
            var barHeight = chartHeight / model.getSeries().length;
            var halfBarHeight = barHeight / 2.0;
            var startYTick = startRY + 9 + barHeight/2;
            var endYTick = endRY - 1 - barHeight/2;

            int tickCount = 10;

            var fnY = NumberScale.domain( 0.0, (double) model.getSeries().length - 1 ).range( startYTick, endYTick );
            var fnX = NumberScale.domain( 0.0, (double) tickCount ).range( startRX, startRX+chartWidth );
            double percent = 100.0/tickCount;
            IntStream.range( 0, tickCount + 1 ).forEach( e -> {
                var d = fnX.apply( e );
                Line2D tickLine = new Line2D.Double( d, endRY, d, endRY +16 );
                graphics.draw( tickLine );
                graphics.drawString( String.format( "%.2f%%",percent * e ), d + 4, endRY + 16 );
            });

            int maxNoOfStacks = 0;

            Font normalFont = graphics.getFont();
            Font smallFont = graphics.getFont().deriveFont( graphics.getFont().getSize()-4f );

            for( int i = 0; i < model.getSeries().length; i++ ) {
                var yPos = fnY.apply(  i );

                graphics.drawString( model.getSeries()[i].getTitle(),
                        startRX - 12 - graphics.getFontMetrics().stringWidth(model.getSeries()[i].getTitle()),
                        yPos + 3 );
                Line2D tickLine = new Line2D.Double( startRX - 6, yPos, startRX, yPos );
                Line2D tickLineEnd = new Line2D.Double( startRX + chartWidth + 3, yPos, startRX + chartWidth + 3  + 6, yPos );

                String value = String.format("%.2f J", model.getSeries()[i].getTotalValue());
                Rectangle totalBounds = getStringBounds( graphics, value, startRX + chartWidth + 3  + 9, yPos );
                graphics.drawString( value, startRX + chartWidth + 3  + 9, yPos + 3 );


                graphics.draw( tickLine );
                graphics.draw( tickLineEnd );
                var stack = model.getSeries()[i];
                var startStack = startRX;
                maxNoOfStacks = Math.max( maxNoOfStacks, stack.getNoOfStacks() );

                for( int j = 0; j < stack.getNoOfStacks(); j++ ) {
                    graphics.setColor( colors[ j % colors.length ] );
                    double ratio = stack.getValueFor( j ) / stack.getTotalValue();
                    int stackWidth = (int) Double.max(1,chartWidth * ratio );
                    if( j == stack.getNoOfStacks() - 1 ) {
                        var diff = (startRX + chartWidth) - (startStack  + 1 + stackWidth);
                        Rectangle2D bar = new Rectangle2D.Double(startStack + 1, yPos - halfBarHeight + 3, stackWidth + diff, barHeight - 6);
                        graphics.fill( bar );
                    }
                    else {
                        Rectangle2D bar = new Rectangle2D.Double(startStack + 1, yPos - halfBarHeight + 3, stackWidth, barHeight - 6);
                        graphics.fill( bar );
                    }

                    graphics.setFont(smallFont);
                    Rectangle stringBounds = getStringBounds( graphics, stack.getValueFor(j) + "", startStack + 6, (int) (yPos) + 3 );
                    if( stringBounds.height < barHeight - 4 ) {
                        //graphics.setColor( Color.WHITE );
                        //graphics.drawString( "" + stack.getValueFor( j ), startStack + stackWidth - stringBounds.width, (int) (yPos) + 4  );
                        graphics.setColor( colors[ j % colors.length ].darker() );
                        graphics.drawString( "" + stack.getValueFor( j ), startStack + stackWidth - stringBounds.width - 1, (int) (yPos) + 3  );
                    }
                    graphics.setFont(normalFont);
                    startStack = startStack + stackWidth;
                }
                graphics.setColor( Color.GRAY );
            }

            double startRXL = startRX + chartWidth/4.0;
            double lastStringWidth = 0.0;
            for( int i=0; i < model.getLegendSize(); i++ ){  // TODO: Provide Legend via Model
                float x = (float) startRXL;
                float y = (float) (startRY - legendHeight/2.0);
                float w = 10, h = 10;
                Rectangle2D legend = new Rectangle2D.Double( x , y , w, 10 );
                Rectangle bounds = getStringBounds( graphics, model.getLegendFor(i) + "", x + 6, y );
                graphics.setColor( colors[ i % colors.length ] );
                graphics.fill( legend );
                graphics.setColor(Color.GRAY);
                graphics.drawString( model.getLegendFor(i), x + w + 3, y + bounds.height / 2 );
                startRXL = startRXL + 20 + bounds.width;
            }
            graphics.setColor( Color.GRAY );
        } else {
            // TODO: print empty text
        }
        graphics.dispose();
    }

    private Rectangle getStringBounds(Graphics2D g2, String str, float x, float y)
    {
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
        return gv.getPixelBounds(null, x, y);
    }
}
