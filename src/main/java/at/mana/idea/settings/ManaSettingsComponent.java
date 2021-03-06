package at.mana.idea.settings;

import at.mana.idea.configuration.ManaRaplConfigurationUtil;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Key;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.*;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ManaSettingsComponent {

    private final JPanel contentPanel;
    private final ActionLink buttonVerify = new ActionLink("Verify MANAi installation");
    private final ActionLink buttonInstallDep = new ActionLink("Install Dependencies");
    private StringBuffer output = new StringBuffer();



    private final ManaSettingsVerifyComponent settingsVerifyComponent = new ManaSettingsVerifyComponent();

    public ManaSettingsComponent() {
        contentPanel = FormBuilder.createFormBuilder()
                .addComponent( buttonVerify )
                .addSeparator( 6 )
                .addComponent( settingsVerifyComponent.getPanelContent(), 6 )
                .addSeparator( 6 )
                .addComponent( buttonInstallDep )
                .addComponentFillVertically( new JPanel(), 1 )
                .getPanel();
        buttonVerify.addActionListener( e -> {
            verifyInstallation();
        } );
        buttonInstallDep.addActionListener( e -> {
            buttonInstallDep.setIcon( new AnimatedIcon.Default() );
            ManaRaplConfigurationUtil.installManaInstrumentPluginAvailable(ProjectManager.getInstance().getDefaultProject(),
                    new ProcessAdapter() {

                        @Override
                        public void processTerminated(@NotNull ProcessEvent event) {
                            if( event.getExitCode() == 0 ) {
                                SwingUtilities.invokeLater( () -> buttonInstallDep.setIcon( AllIcons.Actions.Commit ) );
                            } else {
                                SwingUtilities.invokeLater( () -> buttonInstallDep.setIcon( AllIcons.General.Error ) );
                            }
                        }

                    });
        } );
    }

    public JPanel getPanel() {
        return contentPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return buttonVerify;
    }

    private void verifyInstallation() {
        // Maven Home
        settingsVerifyComponent.getIconLabelHome().setIcon( AllIcons.Actions.Commit );
        if( ManaRaplConfigurationUtil.findMavenHome() != null ) {
            settingsVerifyComponent.getIconLabelHome().setIcon( AllIcons.Actions.Commit );
        } else {
            settingsVerifyComponent.getIconLabelHome().setIcon( AllIcons.General.Error );
        }

        // Maven Home
        settingsVerifyComponent.getIconMvnInstall().setIcon( AllIcons.Actions.Commit );
        if( ManaRaplConfigurationUtil.findExecutablePath("", "mvn") != null ) {
            settingsVerifyComponent.getIconMvnInstall().setIcon( AllIcons.Actions.Commit );
        } else {
            settingsVerifyComponent.getIconMvnInstall().setIcon( AllIcons.General.Error );
        }


        // Port
        settingsVerifyComponent.getIconPortAvail().setIcon( new AnimatedIcon.Default() );
        if( ManaRaplConfigurationUtil.isPortAvailable( 9999 ) ) {
            settingsVerifyComponent.getIconPortAvail().setIcon( AllIcons.Actions.Commit );
        } else {
            settingsVerifyComponent.getIconPortAvail().setIcon( AllIcons.General.Error );
        }

        ManaRaplConfigurationUtil.verifyManaInstrumentPluginAvailable(ProjectManager.getInstance().getDefaultProject(), new ProcessListener() {
            @Override
            public void startNotified(@NotNull ProcessEvent event) {
                output = new StringBuffer();
                SwingUtilities.invokeLater( () -> {
                    settingsVerifyComponent.getIconManaInstrument().setIcon( new AnimatedIcon.Default() );
                });
            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                SwingUtilities.invokeLater( () -> {
                    if( event.getExitCode() == 0 ) {
                        settingsVerifyComponent.getIconManaInstrument().setIcon(AllIcons.Actions.Commit);
                    } else {
                        settingsVerifyComponent.getIconManaInstrument().setIcon(AllIcons.General.Error);
                    }
                });
            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                SwingUtilities.invokeLater( () -> {
                    output.append( event.getText() );
                });
            }
        });
    }

}
