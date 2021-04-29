package at.mana.idea.component.plot;

import java.util.Arrays;

public class DefaultSingleStackedBarPlotModel implements SingleStackedBarPlotModel {

    private String[] legend;
    private Double[] values;


    public DefaultSingleStackedBarPlotModel(String[] legend, Double[] values ) {
        if( legend.length != values.length ) {
            throw new IllegalArgumentException( "legend and values size must match" );
        }
        this.values = values;
        this.legend = legend;
    }

    @Override
    public double getValueFor(int index) {
        return values[index];
    }

    @Override
    public String getLegendFor(int index) {
        return legend[index];
    }

    @Override
    public double getTotalValue() {
        return Arrays.stream( values ).reduce(Double::sum).orElse( 0.0 );
    }

    @Override
    public int getNoOfStacks() {
        return values.length;
    }
}
