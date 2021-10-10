package at.mana.idea.configuration;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ComponentAdapter;

public class ManaRaplConfigurationEditor extends SettingsEditor<ManaRaplConfiguration> implements DumbAware {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JSpinner spinnerSamplesRecorded;
    private JSlider slideroNoSamples;
    private JTextField txtNoSamples;

    public ManaRaplConfigurationEditor() {
        slideroNoSamples.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                txtNoSamples.setText( slideroNoSamples.getValue() + "" );
            }
        });
    }

    @Override
    protected void resetEditorFrom(@NotNull ManaRaplConfiguration s) {
        this.slideroNoSamples.setValue( s.getSamplingRate() );
        this.txtNoSamples.setText( s.getSamplingRate() + "" );
        this.spinnerSamplesRecorded.setValue( s.getNoOfSamples() );
    }

    @Override
    protected void applyEditorTo(@NotNull ManaRaplConfiguration s) throws ConfigurationException {
        s.setNoOfSamples( (int) this.spinnerSamplesRecorded.getValue() ) ;
        s.setSamplingRate( this.slideroNoSamples.getValue() );
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return this.panel1;
    }
}
