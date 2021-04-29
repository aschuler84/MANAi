package at.mana.idea.component.plot;

public interface SingleStackedBarPlotModel {

    double getValueFor( int index );

    String getLegendFor( int index );

    double getTotalValue();

    int getNoOfStacks();

}
