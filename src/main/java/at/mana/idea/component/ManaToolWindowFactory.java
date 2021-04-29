package at.mana.idea.component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import at.mana.idea.service.ManaProjectService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.ui.table.TableView;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ManaToolWindowFactory implements ToolWindowFactory {


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ManaProjectService service = ServiceManager.getService(project,  ManaProjectService.class);
        //TableView<VirtualFile> manaFileList = new TableView<>(  );

        DefaultMutableTreeNode root = new DefaultMutableTreeNode( "mana" );
        for( VirtualFile file: service.findAvailableManaFiles() ) {
            root.add( new DefaultMutableTreeNode(file) );
        }

        Tree manaFileList = new Tree( new DefaultTreeModel( root )  );
        //manaFileList.setModelAndUpdateColumns(
        //        new ListTableModel<>( new ColumnInfo[]{ new IconColumn(), new FileNameColumn() }, service.findAvailableManaFiles(), -1  ));
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator( manaFileList );
        decorator.setToolbarPosition( ActionToolbarPosition.TOP );
        ActionManager actionManager = ActionManager.getInstance();
        decorator.addExtraAction( AnActionButton.fromAction(
                actionManager.getAction("at.mana.idea.component.InitializeManaProject")));
        decorator.disableUpAction();
        decorator.disableDownAction();
        decorator.disableAddAction();
        decorator.disableRemoveAction();

        //manaFileList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        //manaFileList.setBorder(null);
        //manaFileList.setShowGrid( false );
        manaFileList.getEmptyText().setShowAboveCenter(true);
        //manaFileList.setCellSelectionEnabled(false);
        manaFileList.getEmptyText().setText("The current project is not a Mana project - Click Here");
        manaFileList.getEmptyText().getComponent().add( new LinkLabel<String>("Initialize Mana", AllIcons.Ide.Link));
        manaFileList.setCellRenderer( new ManaFileTreeCellRenderer() );
        toolWindow.getComponent().add( decorator.createPanel() );
    }


    private class ManaFileRenderer implements ListCellRenderer<VirtualFile> {

        @Override
        public Component getListCellRendererComponent(JList<? extends VirtualFile> list,
                                                      VirtualFile value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            return new JLabel( value.getName() );
        }
    }

    private class IconColumn extends ColumnInfo<VirtualFile, Object> {
        IconColumn() {
            super(" ");
        }

        @Override
        public String valueOf(VirtualFile item) {
            return null;
        }

        @Override
        public int getWidth(JTable table) {
            return AllIcons.Actions.ProfileCPU.getIconWidth() + 6;
        }


        @Override
        public TableCellRenderer getRenderer(final VirtualFile item) {
            return new DefaultTableCellRenderer(){
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
                    label.setForeground( isSelected ? table.getSelectionForeground() : table.getForeground() );
                    label.setIcon( AllIcons.Actions.ProfileCPU );
                    label.setToolTipText( "Recorded Energy Consumption Profile" );
                    label.setHorizontalAlignment(CENTER);
                    label.setVerticalAlignment(CENTER);
                    return label;
                }
            };
        }
    }

    private class FileNameColumn extends ColumnInfo<VirtualFile, Object>  {

        FileNameColumn() {
            super("Filename");
        }

        @Override
        public @Nullable Object valueOf(VirtualFile virtualFile) {
            return virtualFile.getName();
        }

    }

    private class ManaFileTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if( value instanceof DefaultMutableTreeNode ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if( node.getParent() != null ) {
                    label.setIcon( AllIcons.FileTypes.Any_type );
                    VirtualFile file = (VirtualFile) node.getUserObject();
                    label.setText( file.getName() );
                } else {
                    label.setIcon( AllIcons.Nodes.Folder );
                }
            }
            return label;
        }
    }

}
