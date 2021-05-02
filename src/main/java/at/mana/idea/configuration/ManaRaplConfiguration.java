package at.mana.idea.configuration;

import at.mana.idea.util.OsCheck;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
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

@Getter
@Setter
public class ManaRaplConfiguration extends RunConfigurationBase {

    private int noOfSamples = 10;
    private int samplingRate = 50;
    private String outputFolder;

    public ManaRaplConfiguration(@NotNull Project project, ManaRaplConfigurationFactory factory, String name) {
        super(project, factory, name );
    }

    public String getOutputFolder() {
        if( this.outputFolder == null ) {
            this.outputFolder = getProject().getBasePath() + File.separator + ".mana";
        }
        return this.outputFolder;
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        //return new ManaRaplSettingsEditor();
        return new ManaRaplConfigurationEditor();
    }

    @Override
    public boolean canRunOn(@NotNull ExecutionTarget target) {
        //return OsCheck.getOperatingSystemType().equals(OsCheck.OSType.Linux) || OsCheck.getOperatingSystemType().equals(OsCheck.OSType.MacOS);
        return true;
    }




    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return new ManaRaplRunProfileState( environment );
    }


}
