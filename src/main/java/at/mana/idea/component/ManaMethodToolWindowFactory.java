package at.mana.idea.component;

import at.mana.idea.component.plot.DefaultSingleStackedBarPlotModel;
import at.mana.idea.component.plot.SingleStackedBarPlotComponent;
import at.mana.idea.component.plot.SingleStackedBarPlotModel;
import at.mana.idea.domain.MethodEnergyStatistics;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.service.ManaProjectService;
import at.mana.idea.util.DoubleStatistics;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.ui.table.TableView;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;
import com.intellij.vcs.log.ui.render.GraphCommitCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import java.awt.*;
import javax.swing.*;

public class ManaMethodToolWindowFactory implements ToolWindowFactory {

    // Table listing all method attributions
    //private Tree manaFileList = new Tree();
    //private TableView<MethodEnergyStatistics> methodTable = new TableView<>(  );
    private final Tree methodTree = new Tree();
    private SingleStackedBarPlotComponent barPlotComponent;
    private SingleStackedBarPlotModel barPlotModel;
    private JLabel lblTitle;
    private TreeTable treeTable;
    private ColumnInfo<DefaultMutableTreeNode, String>[] columns;



    private void updateModel( PsiJavaFile file, List<ManaEnergyExperimentModel> data ) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode( file.getClasses()[0] );
        for( ManaEnergyExperimentModel stats: data) {
            stats.getMethodEnergyStatistics().values().forEach( e ->
                    root.add( new DefaultMutableTreeNode(e) )
                    );
        }
        methodTree.setModel( new DefaultTreeModel( root )  );
        /*methodTable.setModelAndUpdateColumns(
                new ListTableModel<>( new ColumnInfo[]{
                        new MethodIconColumn(),
                        new MethodStringColumn<>("Method", methodEnergyStatistics -> "one", false),
                        new MethodStringColumn<>("CPU Power", methodEnergyStatistics -> "two", false),
                        new MethodStringColumn<>("GPU Power", methodEnergyStatistics -> "three", true  ),
                        new MethodStringColumn<>("RAM Power", methodEnergyStatistics -> "three", true  ),
                        new MethodStringColumn<>("Other Power", methodEnergyStatistics -> "three", true  ),
                        new MethodStringColumn<>("Total Power", methodEnergyStatistics -> "four", true  )},
                        data != null ? data : new ArrayList<>(), -1  ));*/
    }

    private JBSplitter createBaseComponent() {
        JBSplitter splitNorthSouth = new JBSplitter( false, "ManaMethodToolWindow.main.divider.proportion", 0.4f );
        splitNorthSouth.setFirstComponent( createLeftComponent() );
        splitNorthSouth.setSecondComponent(createRightComponent());
        splitNorthSouth.setBorder(JBUI.Borders.empty());
        splitNorthSouth.setDividerWidth(1);
        return splitNorthSouth;
    }

    private JPanel createLeftComponent() {
        JPanel leftContent = new JPanel();
        leftContent.setLayout( new BorderLayout() );
        leftContent.add( createDecorator().createPanel() , BorderLayout.CENTER);
        methodTree.setCellRenderer( new ManaMethodTreeCellRenderer() );
        methodTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                        methodTree.getLastSelectedPathComponent();
                if( node == null )
                    return;
                if( node.getUserObject() instanceof PsiClass ) {
                    PsiClass clazz = (PsiClass) node.getUserObject();
                    // TODO: Compute totals
                } else if ( node.getUserObject() instanceof MethodEnergyStatistics ) {
                    MethodEnergyStatistics stats = (MethodEnergyStatistics) node.getUserObject();
                    bind( stats );
                }
            }
        });

        return leftContent;
    }

    private ColumnInfo<DefaultMutableTreeNode, ?>[] getColumns() {
        if( this.columns == null ) {
            columns = new ColumnInfo[]
                    {
                            new TreeTableColumn<String>("Record", TreeTableModel.class, node -> node.getUserObject().toString() ),
                            new TreeTableColumn<String>("Duration", String.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyStatistics ) {
                                    MethodEnergyStatistics statistics = (MethodEnergyStatistics) node.getUserObject();
                                    return statistics.getDurationMillis() + "";
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("CPU Power", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyStatistics ) {
                                    MethodEnergyStatistics statistics = (MethodEnergyStatistics) node.getUserObject();
                                    return statistics.getCpuWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("GPU Power", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyStatistics ) {
                                    MethodEnergyStatistics statistics = (MethodEnergyStatistics) node.getUserObject();
                                    return statistics.getGpuWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("DRAM Power", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyStatistics ) {
                                    MethodEnergyStatistics statistics = (MethodEnergyStatistics) node.getUserObject();
                                    return statistics.getRamWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("Other Power", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyStatistics ) {
                                    MethodEnergyStatistics statistics = (MethodEnergyStatistics) node.getUserObject();
                                    return statistics.getOtherWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<String>("# Samples", String.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyStatistics ) {
                                    MethodEnergyStatistics statistics = (MethodEnergyStatistics) node.getUserObject();
                                    return 1 + "";
                                }
                                return null;
                            }),
                            new TreeTableColumn<String>("Energy", String.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyStatistics ) {
                                    MethodEnergyStatistics statistics = (MethodEnergyStatistics) node.getUserObject();
                                    return "42.42";
                                }
                                return null;
                            })
                    };
        }
        return columns;
    }

    protected void bind( MethodEnergyStatistics energyStatistics ) {

        String[] legend = new String[]{
                String.format("CPU Power %.2f Watt", energyStatistics.getCpuWattage().getAverage()),
                String.format("GPU Power %.2f Watt", energyStatistics.getGpuWattage().getAverage()),
                String.format("DRAM Power %.2f Watt", energyStatistics.getRamWattage().getAverage()),
                String.format("Other Power %.2f Watt", energyStatistics.getOtherWattage().getAverage())};
        Double[] values = new Double[]{
                energyStatistics.getCpuWattage().getAverage(),
                energyStatistics.getGpuWattage().getAverage(),
                energyStatistics.getRamWattage().getAverage(),
                energyStatistics.getOtherWattage().getAverage()};
        barPlotModel = new DefaultSingleStackedBarPlotModel( legend, values );

        if(barPlotComponent != null) {
            barPlotComponent.setModel( barPlotModel );
        }

        if( treeTable != null ) {

            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
            DefaultMutableTreeNode groupA = new DefaultMutableTreeNode( energyStatistics );

            groupA.add( new DefaultMutableTreeNode( "Method A" ) );
            groupA.add( new DefaultMutableTreeNode( "Method B" ) );

            rootNode.add( groupA );

            ListTreeTableModel model = new ListTreeTableModel(rootNode, getColumns() );
            treeTable.setModel( model );
        }

        if( lblTitle != null ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.methodTree.getLastSelectedPathComponent();
            if( node.getUserObject() instanceof MethodEnergyStatistics ){
                MethodEnergyStatistics stats = (MethodEnergyStatistics) node.getUserObject();
                lblTitle.setText( stats.getMethod() != null ? stats.getMethod().getName() : "<???>" );
                lblTitle.setIcon( AllIcons.Nodes.Method );
            }
        }
    }

    private JPanel createRightComponent() {
        JBSplitter splitRigthDetails = new JBSplitter( false, "ManaMethodToolWindow.main.divider.proportion", 0.8f );
        splitRigthDetails.setFirstComponent( createTableSummaryComponent() );
        splitRigthDetails.setSecondComponent( createChartComponent() );
        splitRigthDetails.setBorder(JBUI.Borders.empty());

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( splitRigthDetails, BorderLayout.CENTER );
        return panel;
    }

    private JComponent createTableSummaryComponent() {

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");

        Function<DefaultMutableTreeNode, String> fun = node -> node.getUserObject() != null ? node.getUserObject().toString() : "";


        ListTreeTableModel model = new ListTreeTableModel(rootNode, getColumns() );
        treeTable = new TreeTable(model){
            @Override
            public TreeTableCellRenderer createTableRenderer(TreeTableModel treeTableModel) {
                TreeTableCellRenderer tableRenderer = super.createTableRenderer(treeTableModel);
                tableRenderer.setRootVisible(false);
                tableRenderer.setShowsRootHandles(true);
                tableRenderer.setCellRenderer(new ColoredTreeCellRenderer() {
                    @Override
                    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                        setIcon(AllIcons.Actions.ProfileBlue);
                    }
                });
                return tableRenderer;
            }


        };
        treeTable.setDefaultRenderer( DoubleStatistics.class, new DecimalCellRenderer() );
        treeTable.getTree().setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                setIcon(AllIcons.Actions.ProfileBlue);
            }
        });
        treeTable.setDefaultRenderer( TreeTableModel.class, new ColoredTableCellRenderer() {
            @Override
            protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
                setIcon(AllIcons.Actions.ProfileBlue);
            }
        });
        treeTable.getTree().setShowsRootHandles(true);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator( treeTable );
        ActionManager actionManager = ActionManager.getInstance();
        decorator.addExtraAction( AnActionButton.fromAction(
                actionManager.getAction("at.mana.idea.component.InitializeManaProject")));
        decorator.setToolbarPosition(ActionToolbarPosition.TOP);
        decorator.setScrollPaneBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 0, 1));
        decorator.setToolbarBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 1));
        decorator.setPanelBorder( JBUI.Borders.empty() );
        return decorator.createPanel();
    }

    private JComponent createChartComponent() {
        JBPanel panel = new JBPanel( new BorderLayout() );
        lblTitle = new JLabel();
        lblTitle.setBorder( JBUI.Borders.empty( 3 ) );
        panel.add( lblTitle, BorderLayout.NORTH );
        barPlotComponent = new SingleStackedBarPlotComponent();
        panel.add( barPlotComponent, BorderLayout.CENTER );
        return panel;
    }

    private ToolbarDecorator createDecorator() {
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(methodTree);
        decorator.setToolbarPosition( ActionToolbarPosition.LEFT );
        ActionManager actionManager = ActionManager.getInstance();
        decorator.addExtraAction( AnActionButton.fromAction(
                actionManager.getAction("at.mana.idea.component.InitializeManaProject")));
        decorator.setScrollPaneBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 0, 1));
        decorator.setToolbarBorder( JBUI.Borders.customLine(JBColor.border(), 0, 0, 0, 1));
        decorator.setPanelBorder( JBUI.Borders.empty() );
        decorator.disableUpAction();
        decorator.disableDownAction();
        decorator.disableAddAction();
        decorator.disableRemoveAction();
        return decorator;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorSelectionChangedListener());
        toolWindow.setTitle( "Mana" );
        initTable();
        toolWindow.getComponent().setBorder( JBUI.Borders.empty() );
        toolWindow.getComponent().add( createBaseComponent() );
    }

    private void initTable() {
        methodTree.getEmptyText().setShowAboveCenter(true);
        methodTree.getEmptyText().setText("Select a class file to display recorded energy data");
        methodTree.getEmptyText().getComponent().add( new LinkLabel<String>("Initialize mana", AllIcons.Ide.Link));
    }

    private class FileEditorSelectionChangedListener implements FileEditorManagerListener {
        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event) {
            if( methodTree != null ) {
                if( event.getNewFile() != null ) {
                    Project project = event.getManager().getProject();
                    PsiFile file = PsiManager.getInstance(project).findFile( event.getNewFile() );
                    if( file instanceof PsiJavaFile ) {
                        PsiJavaFile javaFile = (PsiJavaFile) file;
                        ManaProjectService service = ServiceManager.getService(project, ManaProjectService.class);
                        updateModel( javaFile, service.findStatisticsFor( javaFile ));
                    }
                } else {
                    methodTree.getEmptyText().setText("Select a class file to display recorded energy data");
                }
            }
        }
    }

    private class ManaMethodTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if( value instanceof DefaultMutableTreeNode ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if( node.getUserObject() == null ) {
                    return label;
                }
                if( node.getParent() == null ) {
                    label.setIcon( AllIcons.Nodes.Class );
                    PsiClass clazz = (PsiClass) node.getUserObject();
                    label.setText( clazz.getName() );
                } else {
                    label.setIcon( AllIcons.Nodes.Folder );
                    MethodEnergyStatistics stats = (MethodEnergyStatistics) node.getUserObject();
                    label.setIcon( AllIcons.Nodes.Method );
                    label.setText( stats.getMethod() != null ? stats.getMethod().getName() : "<???>" );
                    //label.setText( stats.getMethod() != null ? stats.getMethod().getName() : "<???>" );
                }
            }
            return label;
        }
    }

    private class TreeTableColumn<T> extends ColumnInfo<DefaultMutableTreeNode, T> {

        private final Class<?> columnClass;
        private final Function<DefaultMutableTreeNode, T> function;

        public TreeTableColumn(@NlsContexts.ColumnName String name,
                               @NotNull Class<?> type,
                               @NotNull Function<DefaultMutableTreeNode, T> fun ) {
            super(name);
            this.columnClass = type;
            this.function = fun;
        }

        @Override
        public @Nullable T valueOf(DefaultMutableTreeNode s) {
            return function.apply( s );
        }

        @Override
        public Class<?> getColumnClass() {
            return columnClass;
        }
    }

    private class DecimalCellRenderer extends ColoredTableCellRenderer {
        private DecimalCellRenderer( ) {
        }

        @Override
        protected void customizeCellRenderer(JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
            if (value == null) {
                return;
            }
            this.setTextAlign( SwingConstants.RIGHT );
            if( value instanceof DoubleStatistics ) {
                DoubleStatistics data = (DoubleStatistics) value;
                append( String.format( "(\u00B1%.3f) ", data.getStandardDeviation()), new SimpleTextAttributes( SimpleTextAttributes.STYLE_PLAIN, JBColor.orange ) );
                append( String.format( "%.3f", data.getAverage()), new SimpleTextAttributes( SimpleTextAttributes.STYLE_PLAIN, JBColor.foreground() ), true );
            } else {
                append( value.toString() );
            }
        }
    }

}
