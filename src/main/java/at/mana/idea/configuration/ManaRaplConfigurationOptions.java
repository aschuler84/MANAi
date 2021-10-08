package at.mana.idea.configuration;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;

public class ManaRaplConfigurationOptions extends RunConfigurationOptions {

    //private final StoredProperty<String> myScriptName = string("").provideDelegate(this, "outputfolder");
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
