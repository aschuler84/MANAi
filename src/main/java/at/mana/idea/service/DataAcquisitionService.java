package at.mana.idea.service;

import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface DataAcquisitionService extends ProcessListener {

    static DataAcquisitionService getInstance(@NotNull Project project) {
        return project.getService( DataAcquisitionService.class);
    }

    void startDataAcquisition( @NotNull Project project );

}
