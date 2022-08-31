package at.mana.idea.component;

import at.mana.core.util.DoubleStatistics;
import at.mana.idea.component.plot.*;
import at.mana.idea.model.AnalysisModel;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.model.MethodEnergySampleModel;
import at.mana.idea.service.AnalysisService;
import at.mana.idea.service.EnergyDataNotifierEvent;
import at.mana.idea.service.ManaEnergyDataNotifier;
import at.mana.idea.service.StorageService;
import at.mana.idea.util.ColorUtil;
import at.mana.idea.util.DateUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.*;
import com.intellij.ui.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static at.mana.idea.util.I18nUtil.i18n;

public class ManaMethodToolWindow extends JPanel implements ManaEnergyDataNotifier {

    private final Tree methodTree = new Tree();
    private SingleStackedBarPlotComponent barPlotComponent;
    private MultipleStackedBarPlotComponent multipleBarPlotComponent;
    private SpectrumPlotComponent spectrumPlotComponent;
    private SingleStackedBarPlotModel barPlotModel;
    private MultipleStackedBarPlotModel multipleBarPlotModel;
    private SpectrumPlotModel spectrumPlotModel;
    private JLabel lblTitle;
    private TreeTable treeTable;
    private ColumnInfo<DefaultMutableTreeNode, String>[] columns;
    private ManaEnergyExperimentModel model;
    private AnalysisModel analysisModel;
    private JBTabbedPane tabContainer;

    private void updateModel(PsiJavaFile file, ManaEnergyExperimentModel data, AnalysisModel analysisModel) {
        ReadAction.run( () -> {
            this.model = data;
            this.analysisModel = analysisModel;
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(file.getClasses()[0]);
            if( model != null && !model.getMethodEnergyStatistics().isEmpty() ) {
                // TODO get classes from file -> build parent nodes for each of them
                DefaultMutableTreeNode energyNode = new DefaultMutableTreeNode(model);
                for (var stats : model.getMethodEnergyStatistics().entrySet()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(stats.getKey());
                    energyNode.add(node);
                }
                root.add(energyNode);
                methodTree.setModel(new DefaultTreeModel(root));

                // update model for overview chart
                List<SingleStackedBarPlotModel> series = new ArrayList<>();
                model.getMethodEnergyStatistics().forEach( (k,v) -> {
                    MethodEnergyModel statistics =
                            v.stream().max( Comparator.comparing( MethodEnergyModel::getStartDateTime) ).orElse(null);
                    if( statistics != null ){
                        String[] legend = new String[]{
                                String.format(i18n("methodtoolwindow.ui.chart.cpupower.title"), statistics.getCpuWattage().getAverage()),
                                //String.format(i18n("methodtoolwindow.ui.chart.gpupower.title"), statistics.getGpuWattage().getAverage()),
                                String.format(i18n("methodtoolwindow.ui.chart.drampower.title"), statistics.getRamWattage().getAverage()),
                                String.format(i18n("methodtoolwindow.ui.chart.otherpower.title"), statistics.getOtherWattage().getAverage())};
                        Double[] values = new Double[]{
                                statistics.getCpuWattage().getAverage(),
                                //statistics.getGpuWattage().getAverage(),
                                statistics.getRamWattage().getAverage(),
                                statistics.getOtherWattage().getAverage()};
                        series.add( new DefaultSingleStackedBarPlotModel( k.getName(),  legend, values ) );
                    }
                } );
                series.sort( Comparator.comparing( SingleStackedBarPlotModel::getTotalValue ).reversed() );  // sort by total value
                String[] legend = new String[]{ "CPU", "DRAM", "UNCORE" };
                multipleBarPlotModel = new DefaultMultipleStackedBarPlotModel( legend, series.toArray(SingleStackedBarPlotModel[]::new) );
                SwingUtilities.invokeLater( () ->  multipleBarPlotComponent.setModel( multipleBarPlotModel ) );
            } else {

                root.add( new DefaultMutableTreeNode( "No recorded energy data found" ) );
                SwingUtilities.invokeLater( () -> {
                    methodTree.setModel(new DefaultTreeModel(root));
                    multipleBarPlotComponent.setModel( null );
                    bind(new ArrayList<>());
                });
            }
            if( analysisModel != null && !analysisModel.getComponents().isEmpty() ) {
                DefaultMutableTreeNode analysisNode = new DefaultMutableTreeNode(analysisModel);
                /*for (var stats : analysisModel.getComponents().entrySet()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(stats.getKey());
                    analysisNode.add(node);
                }*/

                root.add( analysisNode );
                methodTree.setModel(new DefaultTreeModel(root));
                spectrumPlotModel = new SpectrumPlotModel( analysisModel );
                SwingUtilities.invokeLater( () ->  spectrumPlotComponent.setModel( spectrumPlotModel ) );
            } else {
                root.add( new DefaultMutableTreeNode( "No analysis data found" ) );
                SwingUtilities.invokeLater( () -> {
                    methodTree.setModel(new DefaultTreeModel(root));
                    spectrumPlotComponent.setModel( null );
                    bind(new ArrayList<>());
                });
            }


        } );
        spectrumPlotComponent.setModel(new SpectrumPlotModel(null));
    }

    private JBSplitter createBaseComponent() {
        JBSplitter splitNorthSouth = new JBSplitter( false, "ManaMethodToolWindow.main.divider.proportion", 0.4f );
        splitNorthSouth.setFirstComponent( createLeftComponent() );
        splitNorthSouth.setSecondComponent( createRightComponent() );
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
                if( node.getUserObject() instanceof PsiClass) {
                    tabContainer.setSelectedIndex(0);
                } else if ( node.getUserObject() instanceof PsiMethod) {
                    List<MethodEnergyModel> stats = model.getMethodEnergyStatistics().get( node.getUserObject() );
                    tabContainer.setSelectedIndex(1);
                    bind( stats );
                } else if( node.getUserObject() instanceof AnalysisModel ) {
                    tabContainer.setSelectedIndex(2);
                } else if( node.getUserObject() instanceof ManaEnergyExperimentModel ) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(0);
                    //List<MethodEnergyModel> stats = model.getMethodEnergyStatistics().get(child.getUserObject() );
                    tabContainer.setSelectedIndex(1);
                    methodTree.setSelectionPath(  new TreePath( child.getPath() ) );
                    //bind( stats );
                }
            }
        });

        return leftContent;
    }

    private ColumnInfo<DefaultMutableTreeNode, ?>[] getColumns() {
        if( this.columns == null ) {
            columns = new ColumnInfo[]
                    {
                            new TreeTableColumn<String>(i18n("methodtoolwindow.ui.table.recorded.title"), TreeTableModel.class, node ->node.getUserObject().toString() ),
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
                            new TreeTableColumn<DoubleStatistics>(i18n("methodtoolwindow.ui.table.energy.title"), DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getEnergyConsumption();
                                }  else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return Arrays.stream( new Double[]{ sample.getEnergyConsumption() + 0.0 } ).collect( DoubleStatistics.collector() );
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>(i18n("methodtoolwindow.ui.table.cpupower.title"), DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getCpuWattage();
                                } else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return sample.getCpuWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>(i18n("methodtoolwindow.ui.table.gpupower.title"), DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getGpuWattage();
                                } else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return sample.getGpuWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>(i18n("methodtoolwindow.ui.table.drampower.title"), DoubleStatistics.class, node -> {
                                if( node.getUserObject() instanceof MethodEnergyModel) {
                                    MethodEnergyModel statistics = (MethodEnergyModel) node.getUserObject();
                                    return statistics.getRamWattage();
                                } else if( node.getUserObject() instanceof MethodEnergySampleModel) {
                                    MethodEnergySampleModel sample = (MethodEnergySampleModel) node.getUserObject();
                                    return sample.getRamWattage();
                                }
                                return null;
                            }),
                            new TreeTableColumn<DoubleStatistics>(i18n("methodtoolwindow.ui.table.otherpower.title"), DoubleStatistics.class, node -> {
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
                    String.format(i18n("methodtoolwindow.ui.chart.cpupower.title"), statistics.getCpuWattage().getAverage()),
                    //String.format(i18n("methodtoolwindow.ui.chart.gpupower.title"), statistics.getGpuWattage().getAverage()),
                    String.format(i18n("methodtoolwindow.ui.chart.drampower.title"), statistics.getRamWattage().getAverage()),
                    String.format(i18n("methodtoolwindow.ui.chart.otherpower.title"), statistics.getOtherWattage().getAverage())};
            Double[] values = new Double[]{
                    statistics.getCpuWattage().getAverage(),
                    //statistics.getGpuWattage().getAverage(),
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
                var i = new AtomicInteger(0);
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

    private JComponent createRightComponent() {
        JBSplitter splitRigthDetails = new JBSplitter( false, "ManaMethodToolWindow.details.divider.proportion", 0.8f );
        splitRigthDetails.setFirstComponent( createTableSummaryComponent() );
        splitRigthDetails.setSecondComponent( createChartComponent() );
        splitRigthDetails.setBorder(JBUI.Borders.empty());

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout( ) );
        multipleBarPlotComponent = new MultipleStackedBarPlotComponent();
        panel.add( multipleBarPlotComponent, BorderLayout.CENTER );

        tabContainer = new JBTabbedPane();
        tabContainer.insertTab( "Overview", null, panel, "", 0 );
        tabContainer.setTabComponentInsets(JBUI.insetsRight(0));

        panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( splitRigthDetails, BorderLayout.CENTER );
        splitRigthDetails.setProportion( 0.8f );
        tabContainer.insertTab( "Method Data", null, panel, "", 1 );

        panel = new JPanel();
        panel.setLayout( new BorderLayout( ));
        spectrumPlotComponent = new SpectrumPlotComponent();
        spectrumPlotComponent.setCellRenderer( (value, column, row, cellComponent) -> {
            Double numericValue = (Double) value;
            String val = String.format( "%.3f", numericValue );
            cellComponent.setText( val );
            final int max = 5;
            int index = (int) ( max * numericValue );
            cellComponent.setFont( cellComponent.getFont().deriveFont( 10f ) );
            cellComponent.setOpaque(true);
            cellComponent.setBackground( ColorUtil.HEAT_MAP_COLORS_DEFAULT[Math.min(index, ColorUtil.HEAT_MAP_COLORS_DEFAULT.length-1)]);
            cellComponent.setBorder( BorderFactory.createEmptyBorder(10,10,10,10) );
            cellComponent.setForeground( cellComponent.getBackground().darker().darker().darker() );
            cellComponent.setHorizontalAlignment( JLabel.CENTER );
            return cellComponent;
        } );
        panel.add( spectrumPlotComponent, BorderLayout.CENTER );
        tabContainer.insertTab( "Spell Analysis", null, panel, "", 2 );

        return tabContainer;
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
        treeTable.setBorder(new JBEmptyBorder(0,0,0,0));
        treeTable.getTree().setShowsRootHandles(true);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator( treeTable );
        ActionManager actionManager = ActionManager.getInstance();
        decorator.addExtraAction( AnActionButton.fromAction(
                actionManager.getAction("at.mana.idea.action.openEditor")));
        decorator.addExtraAction( AnActionButton.fromAction(
                actionManager.getAction("at.mana.idea.action.spellAnalysis")));

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

    public JComponent createContentComponent( Project project ) {
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorSelectionChangedListener());
        project.getMessageBus().connect().subscribe(ManaEnergyDataNotifier.MANA_ENERGY_DATA_NOTIFIER_TOPIC, this);
        JBSplitter baseComponent = this.createBaseComponent();
        this.setLayout( new BorderLayout(0,0) );
        this.add( baseComponent, BorderLayout.CENTER );
        initTable();
        return this;
    }

    private void initTable() {
        methodTree.getEmptyText().setShowAboveCenter(true);
        methodTree.getEmptyText().setText(i18n("methodtoolwindow.ui.table.empty.title"));
    }

    @Override
    public void update(EnergyDataNotifierEvent event) {
        ApplicationManager.getApplication().invokeLater( () -> fillWindowFromModel(event.getProject() ) );
    }

    public void fillWindowFromModel( Project project ) {
        FileEditor editor = FileEditorManager.getInstance(project).getSelectedEditor();
        if( editor != null ) {
            VirtualFile file = editor.getFile();
            if( file != null  ) {
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile instanceof PsiJavaFile) {
                    PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                    StorageService service = StorageService.getInstance(project);
                    AnalysisService analysisService = AnalysisService.getInstance(project);
                    updateModel(javaFile, ReadAction.compute( () -> service.findDataFor(javaFile)),
                            ReadAction.compute( () -> analysisService.findDataFor(javaFile)));
                } else {
                    methodTree.getEmptyText().setText(i18n("methodtoolwindow.ui.table.empty.title"));
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
                AnalysisService analysisService = AnalysisService.getInstance(project);
                updateModel(javaFile, ReadAction.compute( () -> service.findDataFor(javaFile)),
                        ReadAction.compute( () -> analysisService.findDataFor(javaFile)));
            } else {
                methodTree.getEmptyText().setText(i18n("methodtoolwindow.ui.table.empty.title"));
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
                    AnalysisService analysisService = AnalysisService.getInstance(project);
                    updateModel(javaFile, ReadAction.compute( () -> service.findDataFor(javaFile)),
                            ReadAction.compute( () -> analysisService.findDataFor(javaFile)));
                } else {
                    methodTree.getEmptyText().setText(i18n("methodtoolwindow.ui.table.empty.title"));
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
                } else if( node.getUserObject() instanceof AnalysisModel ) {
                    label.setIcon( AllIcons.Actions.Preview );
                    label.setText( "Analysis Data" );
                } else if( node.getUserObject() instanceof  ManaEnergyExperimentModel ) {
                    label.setIcon( AllIcons.Debugger.ThreadStates.Socket );
                    label.setText( "Recorded Energy Data" );
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
                    append(String.format(Locale.ROOT, "(\u00B1%.2f) ", data.getStandardDeviation()), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.orange));
                }
                append(String.format(Locale.ROOT, "%.3f", data.getAverage()), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.foreground()), true);
            } else {
                append( value.toString() );
            }
        }
    }
}
