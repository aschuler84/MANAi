/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.method;

import at.mana.idea.model.MethodEnergyModel;
import com.intellij.icons.AllIcons;
import com.intellij.util.ui.ColumnInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class MethodIconColumn extends ColumnInfo<MethodEnergyModel, String> {

    public MethodIconColumn() {
        super(" ");
    }

    @Override
    public String valueOf(MethodEnergyModel item) {
        return null;
    }

    @Override
    public int getWidth(JTable table) {
        return AllIcons.Nodes.Class.getIconWidth() + 6;
    }


    @Override
    public TableCellRenderer getRenderer(final MethodEnergyModel item) {
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


