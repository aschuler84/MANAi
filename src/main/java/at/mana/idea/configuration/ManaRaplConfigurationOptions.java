/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import com.intellij.execution.application.JvmMainMethodRunConfigurationOptions;
import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;

public class ManaRaplConfigurationOptions extends JvmMainMethodRunConfigurationOptions {

    private final StoredProperty<Integer> noSamplesProperty = property(10).provideDelegate( this, "noOfSamples" );
    private final StoredProperty<Integer> samplingRateProperty = property(50).provideDelegate(this,"samplingRate" );

    public int getNoOfSamples() {
        return noSamplesProperty.getValue(this);
    }

    public int getSamplingRate() {
        return samplingRateProperty.getValue(this);
    }

    public void setNoOfSamples(int noOfSamples ) {
        this.noSamplesProperty.setValue( this, noOfSamples );
    }

    public void setSamplingRate( int samplingRate ) {
        this.samplingRateProperty.setValue( this, samplingRate );
    }

}
