package at.mana.idea.component.plot;

public interface MultipleStackedBarPlotModel {

    String getLegendFor( int index );

    int getLegendSize();

    SingleStackedBarPlotModel[] getSeries();

}
