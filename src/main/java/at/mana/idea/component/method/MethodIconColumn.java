package at.mana.idea.component.method;

import at.mana.idea.domain.MethodEnergyStatistics;
import com.intellij.icons.AllIcons;
import com.intellij.util.ui.ColumnInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MethodIconColumn extends ColumnInfo<MethodEnergyStatistics, String> {

    public MethodIconColumn() {
        super(" ");
    }

    @Override
    public String valueOf(MethodEnergyStatistics item) {
        return null;
    }

    @Override
    public int getWidth(JTable table) {
        return AllIcons.Nodes.Class.getIconWidth() + 6;
    }


    @Override
    public TableCellRenderer getRenderer(final MethodEnergyStatistics item) {
        return new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
                label.setForeground( isSelected ? table.getSelectionForeground() : table.getForeground() );
                label.setIcon( AllIcons.Nodes.Method );
                label.setToolTipText( "Recorded Energy Consumption Profile" );
                label.setHorizontalAlignment(CENTER);
                label.setVerticalAlignment(CENTER);
                return label;
            }
        };
    }
}


