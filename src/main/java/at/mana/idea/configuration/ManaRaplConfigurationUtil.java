package at.mana.idea.configuration;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.util.EnvironmentUtil;

import java.io.File;

public class ManaRaplConfigurationUtil {

    public static String findExecutablePath(String key, String executableName ) {
        var raplHome = EnvironmentUtil.getValue(key);
        if( raplHome == null ) {
            File raplExec = PathEnvironmentVariableUtil.findInPath( executableName );
            if( raplExec != null && raplExec.exists() ) {
                return raplExec.getAbsolutePath();
            }
        } else {
            return raplHome;
        }
        return null;
    }

}
