package at.mana.idea.component.plot;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.util.*;

public class ProjectBrowserPanel extends JPanel {
    private final JBList<String> classList;

    // initiate project browser panel
    public ProjectBrowserPanel () {
        // setup grid layout
        this.setLayout(new GridLayout());

        // create new list model
        DefaultListModel<String> listModel = new DefaultListModel<>();

        // get current project
        Project currentProject = getCurrentProject();

        // check current project and its base path
        if (currentProject != null) {
            if (currentProject.getBasePath() != null) {
                // get class names form current project
                ArrayList<String> classNames = getClassNames(currentProject);

                // add class names to list model
                for (String className : classNames) {
                    listModel.addElement(className);
                }
            }
        }

        // create class list from list model
        classList = new JBList<>(listModel);

        // create scroll pane from class list
        JScrollPane listScrollPane = new JBScrollPane(classList);

        // add scroll pane to the project browser panel
        this.add(listScrollPane);
    }

    // returns the first opened project
    private Project getCurrentProject () {
        // get all opened projects
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();

        // return the first, if there are any
        if (openProjects.length > 0) {
            return openProjects[0];
        } else {
            return null;
        }
    }

    // returns an arraylist of strings, the classes within a project
    private ArrayList<String> getClassNames (Project project) {
        // check project
        if (project == null) { return null; }

        // assume the source "src" folder exists
        String baseSrcPath = project.getBasePath()+"/src";
        File projectDir = new File(project.getBasePath()+"/src");

        // check the sourcecode folder
        if (!projectDir.exists()) { return null; }

        // create iterator and the arraylist to be returned
        Iterator<File> iterator = FileUtils.iterateFilesAndDirs(projectDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        ArrayList<String> classPaths = new ArrayList<>();

        // iterate recursively through project files and packages
        while (iterator.hasNext()) {
            String currPath = iterator.next().toString();                       // get next class file path
            currPath = currPath.replaceAll("\\.java", "");      // remove file extension .java
            currPath = currPath.substring(baseSrcPath.length());                // remeove leading directories
            currPath = project.getName()+currPath;                              // concatenate project name and class name
            currPath = currPath.replace("\\", ".");             // replace \ with .
            classPaths.add(currPath);                                           // add class name to the list
        }

        // sort and return class names
        classPaths.sort(Comparator.naturalOrder());
        return classPaths;
    }

    // returns the classpath of the class, which is currently selected in the project browser panel
    public String getSelectedClass() {
        if (classList == null) { return null; }

        return classList.getSelectedValue();
    }

    // add a selection listener to the project browser panel
    public void addSelectionChangedListener(ListSelectionListener listener) {
        if (classList != null) {
            classList.addListSelectionListener(listener);
        }
    }
}