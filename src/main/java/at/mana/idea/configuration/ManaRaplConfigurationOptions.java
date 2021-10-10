package at.mana.idea.configuration;

import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;

public class ManaRaplConfigurationOptions extends RunConfigurationOptions {

    //private final StoredProperty<String> myScriptName = string("").provideDelegate(this, "outputfolder");
    private final StoredProperty<Integer> noSamplesProperty = property(10).provideDelegate( this, "noOfSamples" );
    private final StoredProperty<Integer> samplingRateProperty = property(50).provideDelegate(this,"samplingRate" );
    private final StoredProperty<String> raplExecutable = string( "execute_rapl_idea" ).provideDelegate( this, "raplExecutable" );

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

    public String getRaplExecutable() {
        return this.raplExecutable.getValue( this );
    }

    public void setRaplExecutable(String raplExecutable) {
        this.raplExecutable.setValue( this, raplExecutable );
    }
}
