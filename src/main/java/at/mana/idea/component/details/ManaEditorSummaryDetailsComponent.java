/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.details;

import at.mana.idea.component.model.ManaEditorModel;
import at.mana.idea.component.plot.BoxPlotComponent;
import at.mana.idea.component.plot.Series;
import at.mana.idea.component.plot.TimeSeriesPlotComponent;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import at.mana.idea.domain.ClassEnergyStatistics;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaEditorSummaryDetailsComponent extends ManaEditorDetailsComponent {

    private TimeSeriesPlotComponent timeSeriesPlot  = new TimeSeriesPlotComponent();
    private BoxPlotComponent boxPlotComponent = new BoxPlotComponent();
    private ManaEditorModel editorModel;
    //private InstantiateTemplateRenderer renderer;

    public ManaEditorSummaryDetailsComponent(VirtualFile file, String title, String description) {
        super(file, title, description);
        editorModel = new ManaEditorModel( this.file );
        //VisualisationService service = ServiceManager.getService(VisualisationService.class);
        //renderer = service.getBean( InstantiateTemplateRenderer.class );
    }

    @Override
    protected JComponent createContent() {
        JBSplitter splitNorthSouth = new JBSplitter( true, "ManaEditorSummaryDetailsComponent.main.divider.proportion", 0.5f );
        splitNorthSouth.setFirstComponent( createTopComponent() );
        splitNorthSouth.setSecondComponent(createBottomComponent());
        splitNorthSouth.setBorder(JBUI.Borders.empty(6));
        return splitNorthSouth;
    }

    private JComponent createTopComponent() {
        JPanel pnlSummary = new JPanel();
        pnlSummary.setLayout( new BorderLayout() );
        pnlSummary.add( createSummaryComponent() , BorderLayout.CENTER);

        JPanel pnlBoxplot = new JPanel();
        pnlBoxplot.setLayout( new BorderLayout() );
        this.boxPlotComponent.setModel(List.of( new Series( new Double[]{1.0,2.0,3.0,4.0,6.0,7.0}, new Double[]{0.5,0.4,0.3,0.5,0.4,0.3,0.2}, "series 1" )) );
        JComponent boxplotComponent = this.boxPlotComponent.createComponent();
        pnlBoxplot.add(boxplotComponent, BorderLayout.CENTER);

        JBSplitter splitLeftRight = new JBSplitter( false, "ManaEditorSummaryDetailsComponent.bottom.divider.proportion", 0.5f );
        splitLeftRight.setFirstComponent( pnlSummary );
        splitLeftRight.setSecondComponent( pnlBoxplot );
        splitLeftRight.setProportion( 0.3f );
        JPanel panelContent = new JPanel();
        panelContent.setLayout( new BorderLayout() );
        panelContent.add( splitLeftRight, BorderLayout.CENTER );
        return panelContent;
    }

    private JComponent createBottomComponent() {
        JPanel pnlSummary = new JPanel();
        pnlSummary.setLayout( new BorderLayout() );
        pnlSummary.setBorder( JBUI.Borders.empty(10) );
        pnlSummary.add( createTopList(), BorderLayout.CENTER );

        JPanel pnlBoxplot = new JPanel();
        pnlBoxplot.setLayout( new BorderLayout() );

        this.timeSeriesPlot.setModel(List.of( new Series( new Double[]{1.0,2.0,3.0,4.0,6.0,7.0}, new Double[]{0.5,0.4,0.3,0.5,0.4,0.3,0.2}, "series 1" )) );
        JComponent timeSeriesComponent = this.timeSeriesPlot.createComponent();
        pnlBoxplot.add(timeSeriesComponent, BorderLayout.CENTER);

        JBSplitter splitLeftRight = new JBSplitter( false, "ManaEditorSummaryDetailsComponent.top.divider.proportion", 0.5f );
        splitLeftRight.setFirstComponent( pnlSummary );
        splitLeftRight.setSecondComponent( pnlBoxplot );
        JPanel panelContent = new JPanel();
        panelContent.setLayout( new BorderLayout() );
        panelContent.add( splitLeftRight, BorderLayout.CENTER );
        return panelContent;
    }

    private JComponent createTopList() {
        JPanel pnlTopList = new JPanel();
        pnlTopList.setLayout( new BorderLayout() );
        JLabel lblTblTitle = new JLabel( "Top 10 energy demanding classes:");
        lblTblTitle.setBorder(JBUI.Borders.emptyBottom(10));
        pnlTopList.add( lblTblTitle, BorderLayout.NORTH );

        TableView<ClassEnergyStatistics> topListTable = new TableView<>(  );
        topListTable.setModelAndUpdateColumns(
                new ListTableModel<>( new ColumnInfo[]{
                        new IconColumn(),
                        new ValueColumn<>("Package", ClassEnergyStatistics::getPackageName, false),
                        new ValueColumn<>("ClassName", ClassEnergyStatistics::getClassName, false),
                        new ValueColumn<>("Energy Consumption", ClassEnergyStatistics::getEnergyConsumption, true  ),
                        new ValueColumn<>("# Methods", ClassEnergyStatistics::getNumberOfMethods, true  )},
                        this.editorModel.getTopXClassEnergyStatistics(10) , -1  ));
        pnlTopList.add( new JBScrollPane( topListTable ), BorderLayout.CENTER );
        return pnlTopList;
    }

    @Nonnull
    private JComponent createSummaryComponent() {
        JPanel summaryComponent = new JPanel();
        summaryComponent.setBorder( JBUI.Borders.empty(10) );
        summaryComponent.setLayout( new GridBagLayout() );
        GridBagConstraints cons = new GridBagConstraints();

        cons.insets = JBUI.insets(6,5,3,5);
        cons.anchor = GridBagConstraints.NORTH;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weightx = 1;
        cons.gridx = 0;
        cons.gridy = 0;

        JLabel lblLibrary = new JLabel( "Library under test:", AllIcons.Nodes.Package, JLabel.LEFT  );
        summaryComponent.add( lblLibrary, cons );

        cons.gridx++;
        cons.gridwidth = 2;

        JLabel lblLibraryValue = new JLabel( "Gson 2.8.5", JLabel.RIGHT );
        summaryComponent.add( lblLibraryValue, cons );

        cons.gridx--;
        cons.gridy++;
        cons.gridwidth = 1;

        JLabel lblClasses = new JLabel( "Number of tested classes:", AllIcons.Nodes.Class, JLabel.LEFT  );
        summaryComponent.add( lblClasses, cons );

        cons.gridx++;
        cons.gridwidth = 2;

        JLabel lblClassesValue = new JLabel( "234", JLabel.RIGHT );
        summaryComponent.add( lblClassesValue, cons );


        cons.gridx--;
        cons.gridy++;
        cons.gridwidth = 1;

        JLabel lblMethods = new JLabel( "Number of tested methods:", AllIcons.Nodes.Method, JLabel.LEFT );
        summaryComponent.add( lblMethods, cons );

        cons.gridx++;
        cons.gridwidth = 2;

        JLabel lblMethodsValue = new JLabel( "234", JLabel.RIGHT );
        summaryComponent.add( lblMethodsValue, cons );

        cons.gridx--;
        cons.gridy++;
        cons.gridwidth = 1;

        JLabel lblApi = new JLabel( "Number of API calls:", AllIcons.Nodes.PpLibFolder, JLabel.LEFT );
        summaryComponent.add( lblApi, cons );

        cons.gridx++;
        cons.gridwidth = 2;

        JLabel lblApiValue = new JLabel( "1234", JLabel.RIGHT );
        summaryComponent.add( lblApiValue, cons );


        cons.gridy++;
        cons.gridx = 0;
        cons.gridwidth = 3;
        cons.weighty = 1;

        summaryComponent.add( new JPanel(), cons );

        return summaryComponent;
    }


    private class IconColumn extends ColumnInfo<ClassEnergyStatistics, String> {
        IconColumn() {
            super(" ");
        }

        @Override
        public String valueOf(ClassEnergyStatistics item) {
            return null;
        }

        @Override
        public int getWidth(JTable table) {
            return AllIcons.Nodes.Class.getIconWidth() + 6;
        }


        @Override
        public TableCellRenderer getRenderer(final ClassEnergyStatistics item) {
            return new DefaultTableCellRenderer(){
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
                    label.setForeground( isSelected ? table.getSelectionForeground() : table.getForeground() );
                    label.setIcon( AllIcons.Nodes.Class );
                    label.setToolTipText( "Recorded Energy Consumption Profile" );
                    label.setHorizontalAlignment(CENTER);
                    label.setVerticalAlignment(CENTER);
                    return label;
                }
            };
        }
    }

    private class ValueColumn<T> extends ColumnInfo<ClassEnergyStatistics, T> {

        private final Function<ClassEnergyStatistics, T> mapper;
        private final boolean numberDecorated;


        public ValueColumn(@NlsContexts.ColumnName String name, Function<ClassEnergyStatistics, T> mapper, boolean numberDecorated) {
            super(name);
            this.numberDecorated = numberDecorated;
            this.mapper = mapper;
        }

        @Override
        public @Nullable T valueOf(ClassEnergyStatistics classEnergyStatistics) {
            return mapper.apply( classEnergyStatistics );
        }

        @Override
        public @Nullable TableCellRenderer getRenderer(ClassEnergyStatistics classEnergyStatistics) {
            return new DefaultTableCellRenderer(){
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setBorder(JBUI.Borders.empty(1, 8));
                    if( !numberDecorated )
                        return label;

                    RoundedCornerPanel panel = new RoundedCornerPanel(15,15);
                    label.setForeground( label.getForeground().brighter() );
                    label.getFont().deriveFont( label.getFont().getSize() - 1f );
                    panel.setLayout( new FlowLayout(FlowLayout.CENTER, 0,0) );
                    panel.setBackground(JBColor.decode("0x327fa8"));
                    panel.add( label );
                    JPanel ret = new JPanel( new FlowLayout(FlowLayout.CENTER ,0,0) );
                    ret.add( panel );
                    ret.setBorder( JBUI.Borders.empty(2,1) );
                    return ret;
                }
            };
        }
    }

    private class RoundedCornerPanel extends JPanel {

        private int arcX = 6;
        private int arcY = 6;

        public RoundedCornerPanel( int arcX, int arcY ) {
            this.arcX = arcX;
            this.arcY = arcY;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Dimension arcs = new Dimension(this.arcX,this.arcY); //Border corners arcs {width,height}, change this to whatever you want
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);//paint background
            graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);//paint border
        }
    }
}
