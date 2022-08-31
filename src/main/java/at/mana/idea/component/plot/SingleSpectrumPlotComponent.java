package at.mana.idea.component.plot;

import at.mana.core.util.NumberScale;
import at.mana.idea.util.ColorUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class SingleSpectrumPlotComponent extends JPanel {

    private Insets insets = JBUI.insets(6);
    private SingleSpectrumPlotModel model;
    private final Color[] colors = ColorUtil.STACK_COLORS_DEFAULT;
    private boolean displayLegend = true;

    public SingleSpectrumPlotComponent() {
        this.setOpaque( true );
    }

    public void setModel(SingleSpectrumPlotModel model) {
        this.model = model;
        this.repaint();
    }

    public void setInsets( Insets insets ) {
        this.insets = insets;
        this.repaint();
    }

    public void setDisplayLegend( boolean display ) {
        this.displayLegend = display;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g.create();
        Font normalFont = graphics.getFont();
        Font smallFont = graphics.getFont().deriveFont( graphics.getFont().getSize()-4f );

        double topSpace = this.getHeight() * 0.1;
        double bottomSpace = this.getHeight() * 0.15;

        int startY = insets.top + (int) topSpace;
        int startX = insets.left;
        int endX = this.getWidth() - insets.right;
        int endY = this.getHeight() - insets.bottom - (int) bottomSpace;

        if( model != null ) {
            int barInsets = 4;
            int rectX = startX + barInsets;
            int rectY = startY + barInsets;
            int rectW = ( endX - barInsets ) - rectX;
            int rectH = ( endY - barInsets ) - rectY;

            //Rectangle2D bar = new Rectangle2D.Double( rectX, rectY, rectW, rectH );
            //LinearGradientPaint gradientPaint = new LinearGradientPaint( 0, 0, rectW, 0,
            //        new float[]{0,1}, new Color[]{ ColorUtil.HEAT_MAP_COLORS_DEFAULT[2], ColorUtil.HEAT_MAP_COLORS_DEFAULT[5] } );
            //graphics.setPaint( gradientPaint );
            int tickCount = 10;
            AtomicInteger counter = new AtomicInteger(0);
            IntStream.range( 0, tickCount + 1 ).forEach(e -> {
                if (e > 0 && e % 2 == 1) {
                    // draw a dashed line
                    graphics.setColor( ColorUtil.HEAT_MAP_COLORS_DEFAULT[counter.get()] );
                    Rectangle2D bar = new Rectangle2D.Double( rectX + (counter.get() * rectW/5 ), rectY, rectW/5, rectH );
                    graphics.fill( bar );
                    counter.incrementAndGet();
                }
            });

            //graphics.fill( bar );

            var fnX = NumberScale.domain( 0.0, (double) tickCount ).range( rectX, rectX + rectW );
            var fnXs = NumberScale.domain( 0.0, (double) 30 ).range( rectX, rectX + rectW );
            var fnYs = NumberScale.domain( 0.0, (double) 5 ).range( rectY, rectY + rectH );
            double percent = 100.0 / tickCount;
            int tickLen = 6;

            graphics.setPaint(null);
            graphics.setColor(JBColor.GRAY);
            graphics.setFont( smallFont );

            /*IntStream.range(1,30).forEach( i -> {
                graphics.setColor( this.getBackground() );
                var d = fnXs.apply(i);
                Line2D dashedLine = new Line2D.Double( d, startY, d, endY );
                graphics.draw(dashedLine);
            });

            IntStream.range(1,5).forEach( i -> {
                graphics.setColor( this.getBackground() );
                var d = fnYs.apply(i);
                Line2D dashedLine = new Line2D.Double( startX, d, endX, d );
                graphics.draw(dashedLine);
            });*/

            graphics.setColor(JBColor.GRAY);

            IntStream.range( 0, tickCount + 1 ).forEach(e -> {
                var d = fnX.apply( e );
                Line2D tickLine = new Line2D.Double( d, endY, d, endY + tickLen );
                graphics.draw( tickLine );

                if( e < tickCount && this.displayLegend ) {
                    String per = String.format("%.0f%%", percent * e);
                    graphics.drawString(per, d + 2, endY + tickLen + 4);
                }

                if( e > 0 && e < tickCount ) {
                    // draw a dashed line
                    Stroke dashed = new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                            0, new float[]{2}, 0);
                    //graphics.setStroke( dashed );
                    graphics.setColor( this.getBackground() );
                    Line2D dashedLine = new Line2D.Double( d, startY, d, endY );
                    graphics.draw( dashedLine );
                    graphics.setStroke( new BasicStroke(0.80f) );  // determine ratio based on largest string
                    graphics.setColor(JBColor.GRAY);
                }
            });
            graphics.setFont(normalFont);

            if( this.model.getNoOfMarkers() > 0 ) {
                IntStream.range( 0, this.model.getNoOfMarkers() ).forEach( i -> {
                    double xPos = rectW * this.model.getMarkerValue( i );
                    Line2D marker = new Line2D.Double( rectX + xPos, startY + 1 , rectX + xPos, endY - 1 );
                    graphics.setStroke(new BasicStroke( 1.6f ));
                    graphics.setColor(JBColor.RED);
                    graphics.draw(marker);
                    graphics.setStroke(new BasicStroke( 0.8f ));
                    Line2D top = new Line2D.Double( rectX + xPos - 3, startY + 1 , rectX + xPos + 3, startY + 1 );
                    graphics.draw(top);
                    top = new Line2D.Double( rectX + xPos - 2, startY + 2 , rectX + xPos + 2, startY + 2 );
                    graphics.draw(top);
                    top = new Line2D.Double( rectX + xPos - 1, startY + 3 , rectX + xPos + 1, startY + 3);
                    graphics.draw(top);
                    Line2D bottom = new Line2D.Double( rectX + xPos - 3, endY -1 , rectX + xPos + 3, endY - 1 );
                    graphics.draw(bottom);
                    bottom = new Line2D.Double( rectX + xPos - 2, endY -2 , rectX + xPos + 2, endY - 2 );
                    graphics.draw(bottom);
                    bottom = new Line2D.Double( rectX + xPos - 1, endY -3 , rectX + xPos + 1, endY - 3 );
                    graphics.draw(bottom);
                });
            }

            Line2D topLine = new Line2D.Double( startX + barInsets, startY, endX - barInsets, startY );
            Line2D bottomLine = new Line2D.Double( startX + barInsets, endY, endX - barInsets, endY );
            Line2D leftLine = new Line2D.Double( startX, startY + barInsets, startX, endY - barInsets );
            Line2D rightLine = new Line2D.Double( endX, startY + barInsets, endX, endY -barInsets );
            graphics.setColor(JBColor.GRAY);
            graphics.setStroke( new BasicStroke(0.80f) );  // determine ratio based on largest string
            graphics.draw( topLine );
            graphics.draw( bottomLine );

        }
        graphics.dispose();
    }


}
