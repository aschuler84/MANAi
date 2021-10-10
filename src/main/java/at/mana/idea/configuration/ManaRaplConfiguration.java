package at.mana.idea.configuration;

import at.mana.idea.util.OsCheck;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.util.EnvironmentUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.function.Consumer;

public class ManaRaplConfiguration extends RunConfigurationBase<ManaRaplConfigurationOptions> {

    private String outputFolder;
    private String raplExecutable;

    public ManaRaplConfiguration(@NotNull Project project, ManaRaplConfigurationFactory factory, String name) {
        super(project, factory, name );
    }

    @Override
    protected @NotNull ManaRaplConfigurationOptions getOptions() {
        return (ManaRaplConfigurationOptions) super.getOptions();
    }

    public String getRaplExecutable() {
        return getOptions().getRaplExecutable();
    }

    public void setRaplExecutable( String raplExecutable ) {
        getOptions().setRaplExecutable( raplExecutable );
    }

    public int getNoOfSamples() {
        return getOptions().getNoOfSamples();
    }

    public void setNoOfSamples(int noOfSamples) {
        getOptions().setNoOfSamples(noOfSamples);
    }

    public int getSamplingRate() {
        return getOptions().getSamplingRate();
    }

    public void setSamplingRate(int samplingRate) {
        getOptions().setSamplingRate( samplingRate );
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getOutputFolder() {
        if( this.outputFolder == null ) {
            this.outputFolder = getProject().getBasePath() + File.separator + ".mana";
        }
        return this.outputFolder;
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new ManaRaplConfigurationEditor();
    }

    @Override
    public boolean canRunOn(@NotNull ExecutionTarget target) {
        return true;
        //return OsCheck.getOperatingSystemType().equals(OsCheck.OSType.Linux) || OsCheck.getOperatingSystemType().equals(OsCheck.OSType.MacOS);
    }


    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new ManaRaplRunProfileState( environment );
    }

    public String findRaplExecutablePath(String key) {
        return ManaRaplConfigurationUtil.findExecutablePath( key, getRaplExecutable() );
    }

    public String findMavenHome( String key ) {
        return ManaRaplConfigurationUtil.findExecutablePath( key, "mvn" );
    }

    private void notifyError( String content ) throws ExecutionException {
        Notification notification = new Notification("ManaNotificationGroup", AllIcons.Debugger.ThreadStates.Socket, NotificationType.ERROR);
        notification.setContent( content );
        Notifications.Bus.notify(notification);
        throw new ExecutionException( content );
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if( findMavenHome( "M2_HOME") == null ) throw new RuntimeConfigurationException("M2_HOME environment variable not specified!");
        if( findRaplExecutablePath( "RAAAAPL_HOME" ) == null ) throw new RuntimeConfigurationException("RAPL_HOME environment variable not specified!");
    }

}
