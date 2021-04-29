package at.mana.idea;

import at.mana.idea.service.ManaProjectService;
import at.mana.idea.domain.MethodEnergyStatistics;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Collection;


/**
 * @author Andreas Schuler
 * ManaMethodMarkerProvider is responsible
 * to annotate lines for energy consumption records
 * are available. The ManaMethodMarkerProvider
 * is executed for every java source file beeing
 * parsed into PSI. During the parsging, once
 * a PSI Method is found the ManaMethodMarkerProvider
 * verifies, if an energy consumption reading is
 * available for respective method.
 *
 * \TODO: Change the icon in accordance IntelliJ Icon guideline
 *
 */
public class ManaMethodMarkerProvider extends RelatedItemLineMarkerProvider {

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        ManaProjectService service = ServiceManager.getService(project,  ManaProjectService.class);
        // This must be an element with a literal expression as a parent
        if ( !( element.getParent() instanceof PsiMethod && element instanceof PsiIdentifier ) ) {
            return;
        }

        PsiMethod method = (PsiMethod) element.getParent();
        MethodEnergyStatistics statistics = service.findStatisticsForMethod( method, element.getContainingFile().getVirtualFile() );
        if( statistics == null ) {
            return;
        }

        // The literal expression must start with the Simple language literal expression
        /*PsiIdentifier identifier = (PsiIdentifier) element;

        if( identifier.getText() != null && !identifier.getText().startsWith( "get" ) ) {
            return;
        }*/
        NavigationGutterIconBuilder<PsiElement> builder =
                NavigationGutterIconBuilder.create(IconLoader.getIcon( "/icons/energy_16.png" ))
                        .setTargets( element )
                        .setTooltipText("Wattage: "
                                + df2.format(statistics.getCpuWattage().getAverage() )
                                + "|"
                                + df2.format(statistics.getGpuWattage().getAverage() )
                                + "|"
                                + df2.format(statistics.getRamWattage().getAverage() )
                                + "|"
                                + df2.format(statistics.getOtherWattage().getAverage() ));
        result.add(builder.createLineMarkerInfo(element));
    }
}
