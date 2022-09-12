package at.mana.idea.component.plot.relativearea;

import java.util.ArrayList;

public class RelativeAreaPlotModel {
    private final ArrayList<RelativeArea> relativeAreas;

    public RelativeAreaPlotModel () {
        this.relativeAreas = new ArrayList<>();
    }

    public RelativeAreaPlotModel (ArrayList<RelativeArea> relativeAreas) {
        this.relativeAreas = relativeAreas;
    }

    public void appendRelativeArea (RelativeArea relativeArea) {
        this.relativeAreas.add(relativeArea);
    }

    public RelativeArea getRelativeAreaAtPosition (int index) {
        if (index >= 0 && index < relativeAreas.size()) {
            return this.relativeAreas.get(index);
        } else {
            throw new IndexOutOfBoundsException("Provided Index was out of bounds!");
        }
    }

    public int getRelativeAreaCount () {
        return this.relativeAreas.size();
    }
}
