package at.mana.idea.component.plot;

public class SingleSpectrumPlotModel {

    private double[] markerValues;

    public SingleSpectrumPlotModel( double[] values ) {
        this.markerValues = values;
    }


    public double getMarkerValue( int pos ) {
        return markerValues[pos];
    }

    public int getNoOfMarkers() {
        return markerValues != null ? markerValues.length : 0;
    }

}
