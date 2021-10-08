package at.mana.idea.service;

import at.mana.idea.model.ManaEnergyExperimentModel;
import com.intellij.openapi.project.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnergyDataNotifierEvent {

    private Project project;
    private ManaEnergyExperimentModel model;

}
