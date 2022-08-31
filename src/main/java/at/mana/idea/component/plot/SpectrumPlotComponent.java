package at.mana.idea.component.plot;

import at.mana.core.util.NumberScale;
import at.mana.idea.util.ColorUtil;
import at.mana.idea.util.PaintUtil;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class SpectrumPlotComponent extends JPanel {

    private final Insets insets = JBUI.insets(20,30);
    private SpectrumPlotModel model;
    private final Color[] colors = ColorUtil.STACK_COLORS_DEFAULT;
    private boolean displayLegend = true;
    private JLabel[][] labels;
    private SpectrumPlotComponentCellRenderer cellRenderer;


    public SpectrumPlotComponent() {
        this.setOpaque( true );
        this.cellRenderer = new DefaultCellRenderer();
    }

    public void setModel(SpectrumPlotModel model) {
        this.model = model;
        if( model != null ) {
            this.labels = new JLabel[model.getColumnCount()][model.getRowCount()];
            this.initLabels();
        }
        this.invalidate();
        this.validate();
        this.repaint();
    }

    public void setCellRenderer( SpectrumPlotComponentCellRenderer cellRenderer ) {
        this.cellRenderer = cellRenderer;
    }

    private void initLabels() {
        this.removeAll();
        for(int i = 0; i < model.getColumnCount(); i++) {
            for(int j = 0; j < model.getRowCount(); j++) {
                if( labels[i][j] == null ) {
                    labels[i][j] = new JLabel();

                }
            }
        }
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

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        //double topSpace = this.getHeight() * 0.1;
        //double bottomSpace = this.getHeight() * 0.15;

        int startY = insets.top; // + (int) topSpace;
        int startX = insets.left;
        int endX = this.getWidth() - insets.right;
        int endY = this.getHeight() - insets.bottom; // - (int) bottomSpace;

        int distanceToYAxis = 10;
        int distanceToXAxis = 10;
        int distanceSeparator = -10;
        int tickLen = 6;

        if( model != null) {
            // draw y-labels and x-labels
            // draw each column
            graphics.setColor( JBColor.GRAY );
            graphics.setPaint(null);
            // draw y-axis label -> find determine left offset
            AtomicInteger offsetYLabel = new AtomicInteger(getMaxWidth( graphics, model.getYLabels() ) + distanceToYAxis);
            AtomicInteger offsetXLabel = new AtomicInteger( getMaxWidth( graphics, model.getXLabels() ) + distanceToXAxis);
            int drawingHeight = endY - (startY + offsetXLabel.get());
            int drawingWidth = endX - (startX + offsetYLabel.get());

            int labelYHeight = model.getYLabels().length != 0 ? drawingHeight / model.getYLabels().length : drawingHeight;
            var fnY = NumberScale.domain( 0.0, (double) model.getYLabels().length ).range( startY + offsetXLabel.get(), endY );

            IntStream.range( 0, model.getYLabels().length ).forEach( i -> {
                String label = model.getYLabels()[i];
                float x = startX;
                float y = fnY.apply(i) + labelYHeight/2;
                int fh = graphics.getFontMetrics().getHeight()/2;
                graphics.drawString(label, x, y + 4 );
                Line2D tickLine = new Line2D.Double(startX + offsetYLabel.get() - tickLen, y, startX + offsetYLabel.get(), y );
                graphics.draw(tickLine);
            } );

            int labelXWidth = model.getXLabels().length != 0 ? drawingWidth / model.getXLabels().length : drawingWidth;
            var fnX = NumberScale.domain( 0.0, (double) model.getXLabels().length ).range( startX + offsetYLabel.get(), endX );

            IntStream.range( 0, model.getXLabels().length ).forEach( i -> {
                String label = model.getXLabels()[i];
                float x = fnX.apply(i) + labelXWidth/2;
                float y = startY + offsetXLabel.get() - distanceToXAxis;
                //int fh = graphics.getFontMetrics().getHeight()/2;
                if( i > 0 ) {
                    int dx = fnX.apply(i);
                    Line2D separator = new Line2D.Double( dx, y + distanceSeparator, dx, endY - distanceSeparator  );
                    //graphics.draw(separator);
                }
                drawRotate(graphics,x,y,-60,label);
            } );

            this.removeAll();
            IntStream.range(0, model.getColumnCount() ).forEach( column -> {
                IntStream.range(0, model.getRowCount() ).forEach( row -> {
                    Object value = model.getValue( column, row );
                    int x = fnX.apply( column );
                    int y = fnY.apply( row );
                    JLabel label = getLabel( column, row );
                    if( label != null ) {
                        JComponent cellComponent = cellRenderer.renderComponent( value, column, row, label);
                        cellComponent.setBounds(startX + offsetYLabel.get() + (labelXWidth*column) +2, startY + offsetXLabel.get() + (labelYHeight*row) + 2, labelXWidth-1, labelYHeight-1);
                        cellComponent.setMinimumSize( new Dimension(6,6) );
                        this.add( cellComponent );
                    }
                });
            });
            Line2D leftLine = new Line2D.Double( startX + offsetYLabel.get(), startY + offsetXLabel.get(), startX + offsetYLabel.get(), endY );
            Line2D rightLine = new Line2D.Double( endX, startY + offsetXLabel.get(), endX, endY );
            graphics.setColor(JBColor.GRAY);
            graphics.setStroke( new BasicStroke(0.80f) );  // determine ratio based on largest string
            graphics.draw( leftLine );
            graphics.draw( rightLine );
        }
        graphics.dispose();
    }

    private JLabel getLabel( int column, int row ) {
        if( labels != null ) {
            return labels[column][row];
        }
        return null;
    }


    private int getMaxWidth( Graphics2D g2, String[] labels ) {
        return Arrays.stream(labels)
            .map( v -> g2.getFontMetrics().stringWidth(v) )
                .max( Integer::compareTo ).get();
    }

    public static void drawRotate(Graphics2D g2d, double x, double y, int angle, String text)
    {
        g2d.translate((float)x,(float)y);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(text,0,0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-(float)x,-(float)y);
    }

    private class DefaultCellRenderer implements SpectrumPlotComponentCellRenderer {

        @Override
        public JComponent renderComponent(Object value, int column, int row, JLabel cellComponent) {
            cellComponent.setText( value + "" );
            cellComponent.setFont( cellComponent.getFont().deriveFont(Font.BOLD) );
            cellComponent.setHorizontalAlignment( JLabel.CENTER );
            cellComponent.setOpaque(true);
            cellComponent.setBackground(ColorUtil.HEAT_MAP_COLORS_SEAGREEN[3]);
            cellComponent.setForeground( cellComponent.getBackground().darker().darker() );
            cellComponent.setBorder( BorderFactory.createLineBorder(cellComponent.getBackground().brighter(), 1) );

            return cellComponent;
        }
    }

}
