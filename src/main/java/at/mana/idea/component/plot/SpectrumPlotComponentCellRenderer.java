package at.mana.idea.component.plot;

import javax.swing.*;

public interface SpectrumPlotComponentCellRenderer {

    JComponent renderComponent( Object value, int column, int row, JLabel cellComponent );

}
