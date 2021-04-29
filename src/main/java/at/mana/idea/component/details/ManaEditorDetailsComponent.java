package at.mana.idea.component.details;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public abstract class ManaEditorDetailsComponent {

    protected final String title;
    protected final String description;
    protected final VirtualFile file;


    public ManaEditorDetailsComponent( VirtualFile file, String title, String description ) {
        this.title = title;
        this.description = description;
        this.file = file;
    }

    public JComponent createComponent() {
        return initializeComponent();
    }

    private JComponent initializeComponent() {
        JPanel detailsPane = new JPanel();
        detailsPane.setLayout( new BorderLayout());
        detailsPane.add( createHeader( ), BorderLayout.NORTH );
        detailsPane.add( createContent( ), BorderLayout.CENTER );
        return detailsPane;
    }

    protected JComponent createHeader() {
        JPanel header = new JPanel();
        header.setLayout( new GridBagLayout() );
        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.weightx = 1;
        cons.gridx = 0;
        header.setBorder(JBUI.Borders.empty(10));
        JLabel lblTitle = new JLabel( title, AllIcons.Nodes.Plugin, JLabel.LEFT  );
        lblTitle.setFont( lblTitle.getFont().deriveFont(18f).deriveFont( Font.BOLD ) );
        lblTitle.setBorder( JBUI.Borders.empty( 10,0 ) );
        header.add( lblTitle, cons );
        JLabel lblLine = new JLabel();
        lblLine.setBorder( JBUI.Borders.customLine( JBColor.border(), 1 ) );
        JLabel lblDescription = new JLabel( description );
        lblDescription.setBorder( JBUI.Borders.empty( 10,0 ) );
        header.add( lblDescription, cons );
        header.add( lblLine, cons );

        return header;
    }

    protected abstract JComponent createContent();

}
