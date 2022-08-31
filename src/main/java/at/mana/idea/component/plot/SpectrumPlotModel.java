package at.mana.idea.component.plot;

import at.mana.idea.model.AnalysisModel;
import com.intellij.psi.PsiMethod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SpectrumPlotModel {

    private AnalysisModel model;
    private List<PsiMethod> methods;
    private static final int MAX_COLUMNS = 3;

    public SpectrumPlotModel(AnalysisModel model) {
        this.model = model;
        if( model != null && model.getComponents() != null && !model.getComponents().isEmpty() ) {
            this.methods = new ArrayList<>(model.getComponents().keySet());
            this.methods.sort(Comparator.comparing(PsiMethod::getName));
        }
    }

    public Object getValue( int column, int row ) {
        if( model != null && methods != null ) {
            switch (row) {
                case 0:
                    return model.getComponents().get(methods.get(column)).getPowerCoefficient();
                case 1:
                    return model.getComponents().get(methods.get(column)).getFrequencyCoefficient();
                case 2:
                    return model.getComponents().get(methods.get(column)).getDurationCoefficient();
                default:
                    return null;
            }
        } else  {
            return null;
        }
    }

    public String[] getYLabels() {
        return new String[]{ "Power", "Frequency (Fqn)", "Time" };
    }

    public String[] getXLabels() {
        return methods != null ? methods.stream().map( PsiMethod::getName ).toArray( String[]::new ) : new String[]{""};
    }

    public int getRowCount() {
        return MAX_COLUMNS;
    }

    public int getColumnCount() {
        return methods != null ? methods.size() : 0;
    }


}
