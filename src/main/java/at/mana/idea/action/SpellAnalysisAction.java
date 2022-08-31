package at.mana.idea.action;

import at.mana.idea.service.AnalysisService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class SpellAnalysisAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AnalysisService service = AnalysisService.getInstance(e.getProject());
        service.analyze( e.getProject() );
    }
}
