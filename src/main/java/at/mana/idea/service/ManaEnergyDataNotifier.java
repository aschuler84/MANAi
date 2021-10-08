package at.mana.idea.service;

import at.mana.idea.model.ManaEnergyExperimentModel;
import com.intellij.util.messages.Topic;

public interface ManaEnergyDataNotifier {

    Topic<ManaEnergyDataNotifier> MANA_ENERGY_DATA_NOTIFIER_TOPIC = Topic.create( "notifies when new energy data is available", ManaEnergyDataNotifier.class );

    void update( EnergyDataNotifierEvent model );


}
