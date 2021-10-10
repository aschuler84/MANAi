package at.mana.idea.configuration;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.EnvironmentUtil;
import com.intellij.xml.util.XmlUtil;

import java.io.File;

public class ManaRaplConfigurationUtil {

    public static final String RAPL_EXECUTABLE_NAME = "execute_rapl_idea";
    public static final String MAVEN_EXECUTABLE_NAME = "mvn";
    public static final String M2_HOME_KEY = "M2_HOME";
    public static final String RAPL_HOME_KEY = "RAPL_HOME";

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

    public static boolean verifyMavenManaPluginAvailable( Project project ) {
        PsiFile[] files = FilenameIndex.getFilesByName(project, "pom.xml", GlobalSearchScope.projectScope(project));
        if( files.length == 0 ) {
            return false;
        } else {
            // TODO implementation wrong...
            for( var file : files ) {
                if( file instanceof XmlFile) {
                    var xmlFile = (XmlFile) file;
                    var rootElement = xmlFile.getRootTag();

                    return !XmlUtil.processXmlElements( rootElement, element -> {
                        if( element instanceof XmlTag) {
                            var xmlElement = (XmlTag) element;
                            if( xmlElement.getName().equals( "plugin" ) ) {
                                var groupId = xmlElement.getSubTagText( "groupId" );
                                var artifactId = xmlElement.getSubTagText( "artifactId" );
                                return !("at.mana".equals( groupId ) && "instrument-maven-plugin".equals(artifactId ));
                            }
                        }
                        return true;
                    }, true );

                }
            }
        }
        return false;
    }

    public static boolean verifyMavenManaPluginAvailable( Project project, XmlFile pomFile ) {
        var rootElement = pomFile.getRootTag();
        return !XmlUtil.processXmlElements( rootElement, element -> {
            if( element instanceof XmlTag) {
                var xmlElement = (XmlTag) element;
                if( xmlElement.getName().equals( "plugin" ) ) {
                    var groupId = xmlElement.getSubTagText( "groupId" );
                    var artifactId = xmlElement.getSubTagText( "artifactId" );
                    return !("at.mana".equals( groupId ) && "instrument-maven-plugin".equals(artifactId ));
                }
            }
            return true;
        }, true );
    }

}
