/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.service;

import at.mana.idea.model.ManaEnergyExperimentModel;
import com.intellij.util.messages.Topic;

public interface ManaEnergyDataNotifier {

    Topic<ManaEnergyDataNotifier> MANA_ENERGY_DATA_NOTIFIER_TOPIC = Topic.create( "notifies when new energy data is available", ManaEnergyDataNotifier.class );

    void update( EnergyDataNotifierEvent model );


}
