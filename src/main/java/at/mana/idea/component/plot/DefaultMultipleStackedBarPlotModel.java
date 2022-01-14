package at.mana.idea.component.plot;

public class DefaultMultipleStackedBarPlotModel implements MultipleStackedBarPlotModel{

    private String[] legend;
    private SingleStackedBarPlotModel[] series;

    public DefaultMultipleStackedBarPlotModel(String[] legend, SingleStackedBarPlotModel[] series) {
        this.legend = legend;
        this.series = series;
    }

    @Override
    public String getLegendFor(int index) {
        return legend[index];
    }

    @Override
    public int getLegendSize() {
        return legend.length;
    }

    @Override
    public SingleStackedBarPlotModel[] getSeries() {
        return series;
    }
}
