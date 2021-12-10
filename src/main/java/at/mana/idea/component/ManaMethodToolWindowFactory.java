package at.mana.idea.component;

import at.mana.idea.component.plot.DefaultSingleStackedBarPlotModel;
import at.mana.idea.component.plot.SingleStackedBarPlotComponent;
import at.mana.idea.component.plot.SingleStackedBarPlotModel;
import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.model.MethodEnergySampleModel;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.service.EnergyDataNotifierEvent;
import at.mana.idea.service.ManaEnergyDataNotifier;
import at.mana.idea.service.StorageService;
import at.mana.core.util.DoubleStatistics;
import at.mana.idea.util.DateUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.*;
import com.intellij.ui.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import java.awt.*;
import javax.swing.*;

public class ManaMethodToolWindowFactory implements ToolWindowFactory, ManaEnergyDataNotifier {

    private final Tree methodTree = new Tree();
    private SingleStackedBarPlotComponent barPlotComponent;
    private SingleStackedBarPlotModel barPlotModel;
    private JLabel lblTitle;
    private TreeTable treeTable;
    private ColumnInfo<DefaultMutableTreeNode, String>[] columns;
    private ManaEnergyExperimentModel model;

    private void updateModel( PsiJavaFile file, ManaEnergyExperimentModel data ) {
        this.model = data;
        if( model != null && !model.getMethodEnergyStatistics().isEmpty() ) {
            // TODO get classes from file -> build parent nodes for each of them
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(file.getClasses()[0]);
            for (var stats : model.getMethodEnergyStatistics().entrySet()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(stats.getKey());
                root.add(node);
            }
            methodTree.setModel(new DefaultTreeModel(root));
        } else {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(file.getClasses()[0]);
            root.add( new DefaultMutableTreeNode( "No recorded energy data found" ) );
            methodTree.setModel(new DefaultTreeModel(root));
            bind( new ArrayList<>() );
        }
    }

    private JBSplitter createBaseComponent() {
        JBSplitter splitNorthSouth = new JBSplitter( false, "ManaMethodToolWindow.main.divider.proportion", 0.4f );
        splitNorthSouth.setFirstComponent( createLeftComponent() );
        splitNorthSouth.setSecondComponent(createRightComponent());
        splitNorthSouth.setBorder(JBUI.Borders.empty());
        splitNorthSouth.setDividerWidth(1);
        splitNorthSouth.setProportion( 0.2f );
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
                } else if ( node.getUserObject() instanceof PsiMethod ) {
                    List<MethodEnergyModel> stats = model.getMethodEnergyStatistics().get( node.getUserObject() );
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
                            new TreeTableColumn<String>("Recorded", TreeTableModel.class, node ->node.getUserObject().toString() ),
                            new TreeTableColumn<DoubleStatistics>("Duration", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getDuration();
                                }  else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return Arrays.stream( new Double[]{ sample.getDuration() + 0.0 } ).collect( DoubleStatistics.collector() );
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("Energy Consumption", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getEnergyConsumption();
                                }  else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return Arrays.stream( new Double[]{ sample.getEnergyConsumption() + 0.0 } ).collect( DoubleStatistics.collector() );
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("CPU Power", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getCpuWattage();
                                } else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return sample.getCpuWattage();
                                 }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("GPU Power", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getGpuWattage();
                                } else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return sample.getGpuWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("DRAM Power", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getRamWattage();
                                } else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return sample.getRamWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>("Other Power", DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getOtherWattage();
                                } else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return sample.getOtherWattage();
                                }
                                return null;
                            })
                    };
        }
        return columns;
    }

    protected void bind( List<MethodEnergyModel> energyStatistics ) {

        MethodEnergyModel statistics =
                energyStatistics.stream().max( Comparator.comparing( MethodEnergyModel::getStartDateTime) ).orElse(null);
        if( statistics != null ) {
            String[] legend = new String[]{
                    String.format("CPU Power %.2f Watt", statistics.getCpuWattage().getAverage()),
                    String.format("GPU Power %.2f Watt", statistics.getGpuWattage().getAverage()),
                    String.format("DRAM Power %.2f Watt", statistics.getRamWattage().getAverage()),
                    String.format("Other Power %.2f Watt", statistics.getOtherWattage().getAverage())};
            Double[] values = new Double[]{
                    statistics.getCpuWattage().getAverage(),
                    statistics.getGpuWattage().getAverage(),
                    statistics.getRamWattage().getAverage(),
                    statistics.getOtherWattage().getAverage()};
            barPlotModel = new DefaultSingleStackedBarPlotModel(legend, values);

            if (barPlotComponent != null) {
                barPlotComponent.setModel(barPlotModel);
            }
        } else {
            barPlotComponent.setModel( null );
        }


        if( treeTable != null ) {

            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");

            energyStatistics.forEach( methodEnergyStatistics -> {
                DefaultMutableTreeNode group = new DefaultMutableTreeNode( methodEnergyStatistics );
                methodEnergyStatistics.getSamples()
                        .forEach( n -> group.add( new DefaultMutableTreeNode( n ) ) );
                rootNode.add( group );
            } );

            ListTreeTableModel model = new ListTreeTableModel(rootNode, getColumns() );
            treeTable.setModel( model );
            treeTable.getTree().expandRow(1);
        }

        if( lblTitle != null ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.methodTree.getLastSelectedPathComponent();
            if( node != null && node.getUserObject() instanceof PsiMethod ){
                PsiMethod stats = (PsiMethod) node.getUserObject();
                lblTitle.setText(stats.getName() );
                lblTitle.setIcon( AllIcons.Nodes.Method );
            } else {
                lblTitle.setText( "" );
                lblTitle.setIcon( null );
            }
        }
    }

    private JPanel createRightComponent() {
        JBSplitter splitRigthDetails = new JBSplitter( false, "ManaMethodToolWindow.details.divider.proportion", 0.8f );
        splitRigthDetails.setFirstComponent( createTableSummaryComponent() );
        splitRigthDetails.setSecondComponent( createChartComponent() );
        splitRigthDetails.setBorder(JBUI.Borders.empty());

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( splitRigthDetails, BorderLayout.CENTER );
        splitRigthDetails.setProportion( 0.8f );
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

                        if( value instanceof  DefaultMutableTreeNode ) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                            if (node.getUserObject() instanceof MethodEnergyModel) {
                                MethodEnergyModel data = (MethodEnergyModel) node.getUserObject();
                                append(data.getStartDateTime().format( DateUtil.Formatter ));
                                setIcon(AllIcons.Actions.ProfileBlue);
                            } else if (node.getUserObject() instanceof MethodEnergySampleModel) {
                                MethodEnergySampleModel data = (MethodEnergySampleModel) node.getUserObject();
                                append( row + "");
                                setIcon(AllIcons.Xml.Html_id);
                            }
                        } else {
                            append(value.toString());
                        }
                    }
                });
                return tableRenderer;
            }


        };
        treeTable.setDefaultRenderer( DoubleStatistics.class, new DecimalCellRenderer() );

        treeTable.getTree().setShowsRootHandles(true);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator( treeTable );
        //ActionManager actionManager = ActionManager.getInstance();
        //decorator.addExtraAction( AnActionButton.fromAction(
        //        actionManager.getAction("at.mana.idea.component.InitializeManaProject")));
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
        //ActionManager actionManager = ActionManager.getInstance();
        //decorator.addExtraAction( AnActionButton.fromAction(
        //        actionManager.getAction("at.mana.idea.component.InitializeManaProject")));
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
        project.getMessageBus().connect().subscribe(ManaEnergyDataNotifier.MANA_ENERGY_DATA_NOTIFIER_TOPIC, this);
        toolWindow.setTitle( "Mana" );
        initTable();
        toolWindow.getComponent().setBorder( JBUI.Borders.empty() );
        toolWindow.getComponent().add( createBaseComponent() );
        // execute in background
        fillWindowFromModel( project );
    }

    private void initTable() {
        methodTree.getEmptyText().setShowAboveCenter(true);
        methodTree.getEmptyText().setText("Select a class file to display recorded energy data");
        methodTree.getEmptyText().getComponent().add( new LinkLabel<String>("Initialize mana", AllIcons.Ide.Link));
    }

    @Override
    public void update(EnergyDataNotifierEvent event) {
        ApplicationManager.getApplication().invokeLater( () -> fillWindowFromModel(event.getProject() ) );
    }

    private void fillWindowFromModel( Project project ) {
        FileEditor editor = FileEditorManager.getInstance(project).getSelectedEditor();
        if( editor != null ) {
            VirtualFile file = editor.getFile();
            if( file != null  ) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile instanceof PsiJavaFile) {
                    PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                    StorageService service = StorageService.getInstance(project);
                    updateModel(javaFile, service.findDataFor(javaFile));
                } else {
                    methodTree.getEmptyText().setText("Select a class file to display recorded energy data");
                }
            }
        }
    }

    private class FileEditorSelectionChangedListener implements FileEditorManagerListener {

        @Override
        public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
            Project project = source.getProject();
            PsiFile psiFile = PsiManager.getInstance(project).findFile( file );
            if( psiFile instanceof PsiJavaFile ) {
                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                StorageService service = StorageService.getInstance(project);
                updateModel( javaFile, service.findDataFor( javaFile ));
            } else {
                methodTree.getEmptyText().setText("Select a class file to display recorded energy data");
            }
        }

        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                if( event.getNewFile() != null ) {
                    Project project = event.getManager().getProject();
                    PsiFile file = PsiManager.getInstance(project).findFile( event.getNewFile() );
                    if( file instanceof PsiJavaFile ) {
                        PsiJavaFile javaFile = (PsiJavaFile) file;
                        StorageService service = StorageService.getInstance(project);
                        updateModel( javaFile, service.findDataFor( javaFile ));
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
                } else if( node.getUserObject() instanceof PsiMethod ) {
                    label.setIcon( AllIcons.Nodes.Folder );
                    PsiMethod method = (PsiMethod) node.getUserObject();
                    label.setIcon( AllIcons.Nodes.Method );
                    label.setText( method.getName() );
                } else if( node.getUserObject() instanceof String ) {
                    label.setIcon( AllIcons.General.Error );
                    label.setText( node.getUserObject().toString() );
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
                if( data.getCount() > 1 ) {
                    append(String.format("(\u00B1%.2f) ", data.getStandardDeviation()), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.orange));
                }
                append(String.format("%.3f", data.getAverage()), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.foreground()), true);
            } else {
                append( value.toString() );
            }
        }
    }
}
