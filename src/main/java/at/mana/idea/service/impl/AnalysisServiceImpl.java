package at.mana.idea.service.impl;

import at.mana.idea.analysis.SpellAnalysis;
import at.mana.idea.domain.Analysis;
import at.mana.idea.domain.Measurement;
import at.mana.idea.service.AnalysisService;
import at.mana.idea.util.HibernateUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static at.mana.idea.util.I18nUtil.i18n;

public class AnalysisServiceImpl implements AnalysisService {

    private static final Logger logger = Logger.getInstance( DataAcquisitionServiceImpl.class );
    private BackgroundableProcessIndicator indicator;

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
                            "Select m from Measurement m join m.samples s join s.trace", Measurement.class)
                            .getResultList();
                    Analysis analysis = new SpellAnalysis().compute( measurements );
                    session.save( analysis );
                    return analysis;
                });
            }
        };
        indicator = new BackgroundableProcessIndicator( project, task );
        ProgressManager.getInstance().runProcessWithProgressAsynchronously( task, indicator );
    }
}
