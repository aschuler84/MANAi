package at.mana.idea.service.impl;

import at.mana.core.util.KeyValuePair;
import at.mana.idea.analysis.SpellAnalysis;
import at.mana.idea.domain.Analysis;
import at.mana.idea.domain.AnalysisComponent;
import at.mana.idea.domain.Measurement;
import at.mana.idea.model.AnalysisModel;
import at.mana.idea.model.AnalysisModelComponent;
import at.mana.idea.service.AnalysisService;
import at.mana.idea.util.HibernateUtil;
import at.mana.idea.util.MethodUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

import static at.mana.idea.util.I18nUtil.i18n;

public class AnalysisServiceImpl implements AnalysisService {

    private static final Logger logger = Logger.getInstance( DataAcquisitionServiceImpl.class );
    private BackgroundableProcessIndicator indicator;
    private final Map<PsiJavaFile, AnalysisModel> model = new HashMap<>();
    private PsiMethod selectedMethod;


    @Override
    public void analyze(Project project) {
        if( indicator != null && indicator.isRunning() ) {
            throw new RuntimeException(i18n("analysis.mana.exception"));
        }
        var task = new Task.Backgroundable( project, i18n("analysis.mana.title") ){
            @SneakyThrows
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                HibernateUtil.executeInTransaction( session -> {
                    List<Measurement> measurements = session.createQuery(
                            "Select m from Measurement m", Measurement.class)
                            .getResultList();  // TODO: instead load all measurements of latest run?
                    Analysis analysis = new SpellAnalysis().compute( measurements );
                    session.save( analysis );
                    invalidateModel();
                    return analysis;
                });
            }
        };
        indicator = new BackgroundableProcessIndicator( project, task );
        ProgressManager.getInstance().runProcessWithProgressAsynchronously( task, indicator );
    }

    @Override
    public AnalysisModel findDataFor(PsiJavaFile file) {
        if( DumbService.isDumb( file.getProject() ) )
            return null;  // return null during indices are being built
        if( model.get( file ) == null ) {
            AnalysisModel aModel = model.computeIfAbsent(file, psiFile -> new AnalysisModel());
            List<KeyValuePair<PsiMethod, String>> keys = MethodUtil.buildHashFromMethods(file);

            HibernateUtil.executeInTransaction(session -> {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Analysis> query = builder.createQuery(Analysis.class);
                Root<Analysis> root = query.from(Analysis.class);
                query = query.select(root).orderBy(builder.desc(root.get("created")));
                Optional<Analysis> optResult = session.createQuery(query).setMaxResults(1)
                        .getResultList().stream().findFirst();
                if (optResult.isPresent()) {
                    for (AnalysisComponent component : optResult.get().getComponents()) {
                        Optional<KeyValuePair<PsiMethod, String>> matchOpt =
                                keys.stream().filter(k -> k.getValue().equals(component.getDescriptor().getHash())).findFirst();
                        if (matchOpt.isPresent()) {
                            PsiMethod matchingMethod = matchOpt.get().getKey();
                            aModel.getComponents().computeIfAbsent(matchingMethod, m -> fromAnalysisComponent(component));
                        }
                    }
                }
                return model;
            });
        }
        return model.get( file );
    }

    private AnalysisModelComponent fromAnalysisComponent(AnalysisComponent component) {
        AnalysisModelComponent modelComponent = new AnalysisModelComponent();
        modelComponent.setPowerCoefficient( component.getComponentValues().get(0) );
        modelComponent.setFrequencyCoefficient( component.getComponentValues().get(1) );
        modelComponent.setDurationCoefficient( component.getComponentValues().get(2) );
        return modelComponent;
    }

    private void invalidateModel(){
        this.model.clear();
    }

    public void setSelectedMethod( PsiMethod method ) {
        this.selectedMethod = method;
    }

    public void clearSelectedMethod(  ) {
        this.selectedMethod = null;
    }

    @Override
    public boolean hasSelectedMethod( PsiMethod method ) {
        return this.selectedMethod != null && this.selectedMethod.equals(method);
    }
}
