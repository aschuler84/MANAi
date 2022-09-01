package at.mana.idea.component.inline;

import at.mana.idea.component.plot.SingleSpectrumPlotComponent;
import at.mana.idea.component.plot.SingleSpectrumPlotModel;
import at.mana.idea.service.EnergyDataNotifierEvent;
import at.mana.idea.service.ManaEnergyDataNotifier;
import at.mana.idea.util.SpringUtilities;
import com.intellij.util.ui.JBInsets;

import javax.swing.*;
import java.awt.*;

public class SpectrumInlinePlotComponent {

    private final SingleSpectrumPlotComponent[] plotComponents = new SingleSpectrumPlotComponent[ ] {
            new SingleSpectrumPlotComponent(),
            new SingleSpectrumPlotComponent(),
            new SingleSpectrumPlotComponent() };
    private JPanel container;

    private static final String[] LABELS = new String[]{"(P)","(Fqn)","(T)"};

    public void updateModel( int index, SingleSpectrumPlotModel model ) {
        if( index >= plotComponents.length || index < 0 )
            return;
        plotComponents[index].setModel( model );
    }

    public JComponent getComponent(  ) {
        if( container == null ) {
            container = new JPanel(new BorderLayout());
            container.setOpaque(false);
            JPanel panel = new JPanel(new SpringLayout());
            panel.setOpaque(false);
            for (int i = 0; i < 3; i++) {
                JPanel panelPlot = new JPanel(new FlowLayout());
                JLabel l = new JLabel(LABELS[i], JLabel.LEFT);
                l.setVerticalAlignment( JLabel.CENTER );
                l.setFont( l.getFont().deriveFont(7f) );

                SingleSpectrumPlotComponent plot = plotComponents[i];
                plot.setInsets(JBInsets.create(0, 0));
                plot.setDisplayLegend(false);
                plot.setBackground(new Color(0, 0, 0, 0));
                plot.setModel(new SingleSpectrumPlotModel(new double[]{0.45}));
                plot.setPreferredSize(new Dimension(70, 20));
                l.setLabelFor(plot);
                l.setBorder(BorderFactory.createEmptyBorder(10,0,0, i < 2 ? 30 : 5));
                panel.add(plot);
                panel.add(l);
            }

            SpringUtilities.makeCompactGrid(panel,
                    1, 6, //rows, cols
                    0, 0,        //initX, initY
                    0, 0);       //xPad, yPad

            container.add(panel, BorderLayout.CENTER);
        }
        return container;
    }


}
