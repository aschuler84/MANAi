package at.mana.idea.component.popup;

import at.mana.idea.component.plot.SingleSpectrumPlotComponent;
import at.mana.idea.component.plot.SingleSpectrumPlotModel;
import at.mana.idea.util.SpringUtilities;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SpectrumPopUpComponent {

    private final SingleSpectrumPlotComponent[] plotComponents = new SingleSpectrumPlotComponent[ ] {
            new SingleSpectrumPlotComponent(),
            new SingleSpectrumPlotComponent(),
            new SingleSpectrumPlotComponent() };

    private static final String[] LABELS = new String[]{"Power (P)","Frequency (Fqn)","Time (T)"};

    public void updateModel( int index, SingleSpectrumPlotModel model ) {
        if( index >= plotComponents.length || index < 0 )
            return;
        plotComponents[index].setModel( model );
    }

    public JComponent getComponent() {
        JPanel container = new JPanel( new BorderLayout() );
        JPanel panel = new JPanel(new SpringLayout());
        for (int i = 0; i < 3; i++) {
            JLabel l = new JLabel(LABELS[i], JLabel.TRAILING);
            panel.add(l);
            SingleSpectrumPlotComponent plot = plotComponents[i];
            plot.setModel( new SingleSpectrumPlotModel( new double[]{0.45} ) );
            plot.setPreferredSize( new Dimension( 300, 40 ));
            l.setLabelFor(plot);
            panel.add(plot);
        }

        SpringUtilities.makeCompactGrid(panel,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad

        JLabel title = new JLabel("SPELL Analysis Results");
        title.setBorder(JBUI.Borders.empty(6,6,0,6));
        title.setFont( title.getFont().deriveFont( 14f ).deriveFont( Font.BOLD ) );
        JLabel subTitle = new JLabel("<html><p>Reported results are computed based on the <em>SPELLing Out Energy Leaks - <br>Aiding Developers Locate Energy Inefficient Code</em> article by Pereira et al.</p></html>");
        subTitle.setBorder(JBUI.Borders.empty(6,6,6,6));
        JPanel header = new JPanel(new BorderLayout());
        header.add( title, BorderLayout.NORTH );
        header.add( subTitle, BorderLayout.CENTER );
        header.add( new JSeparator( JSeparator.HORIZONTAL ), BorderLayout.SOUTH);
        container.add( header, BorderLayout.NORTH);
        container.add( panel, BorderLayout.CENTER );
        return container;
    }



}
