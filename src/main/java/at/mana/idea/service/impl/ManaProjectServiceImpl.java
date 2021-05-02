package at.mana.idea.service.impl;

import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.service.ManaProjectService;
import at.mana.idea.domain.MethodEnergyStatistics;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static at.mana.idea.util.MatrixOperations.transpose;

@Service
public class ManaProjectServiceImpl implements ManaProjectService {

    private final static String MANA_NAME_SUFFIX = ".mana";
    private VirtualFile selectedManaTraceFile;
    private final Project project;
    private Map<PsiClass,ManaEnergyExperimentModel> energyStatsModel = new HashMap<>();
    public ManaProjectServiceImpl(Project project) {
        this.project = project;
    }
    public static final SimpleDateFormat FOLDER_DATE = new SimpleDateFormat("ddMMyyyyHHmmss");
    private static final Logger logger = Logger.getInstance( ManaProjectServiceImpl.class );


    @Override  // executed whenever a file is changed
    public void after(@NotNull List<? extends VFileEvent> events) {
        for( VFileEvent event : events ) {
            if( event.getFile() != null &&
                    event.getFile().getName().endsWith( MANA_NAME_SUFFIX ) ) {
                // search if a mana file has changed - if so overwrite the stats
                if( isValidManaFile( event.getFile() ) ) {
                    VirtualFile folder = event.getFile().getParent();
                    try {
                        LocalDateTime recorded = FOLDER_DATE.parse(folder.getName()).toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();

                        String className = event.getFile().getName().substring(0, event.getFile().getName().lastIndexOf('.')).replace("_", ".");
                        PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.projectScope(project));
                        String methodName = event.getFile().getName().substring( event.getFile().getName().indexOf( '_' ), event.getFile().getName().lastIndexOf('.') );
                        if (clazz != null) {
                            PsiMethod method = Arrays.stream(clazz.getMethods()).filter( psiMethod -> psiMethod.getName().equals( methodName ) ).findFirst().get();
                            energyStatsModel.computeIfAbsent(clazz, c -> new ManaEnergyExperimentModel());
                            ManaEnergyExperimentModel model = this.energyStatsModel.get(clazz);

                            computeStatistics(model, recorded, method, event.getFile());
                        }

                    } catch( ParseException e ) {
                        logger.warn( "Could not parse date from folder name: " + folder.getName(), e );
                    }
                }
             }
        }
    }

    private boolean isValidManaFile( VirtualFile file ) {
        return file.getName().endsWith(MANA_NAME_SUFFIX)
                && file.getParent().getName().equals(MANA_NAME_SUFFIX);
    }


    @Override
    public MethodEnergyStatistics findStatisticsForMethod(PsiMethod method, VirtualFile file ) {
        PsiClass clazz = method.getContainingClass();

        if( method.getNameIdentifier() != null ) {
            String methodName = method.getNameIdentifier().getText();
            if (clazz != null) {
                    ManaEnergyExperimentModel model = energyStatsModel.get(clazz);
                    return model != null ? model.getMethodEnergyStatistics().get(method) : null;
            }
        }
        return null;
    }

    public List<ManaEnergyExperimentModel> findStatisticsFor( PsiJavaFile file ) {

        return Arrays.stream( file.getClasses() ).map( c-> energyStatsModel.get(c) ).filter( Objects::nonNull ).collect( Collectors.toList() );
    }

    private void computeStatistics(ManaEnergyExperimentModel model, LocalDateTime recorded, PsiMethod method, VirtualFile f) {
        try {
            JsonObject jsonTree = (JsonObject) JsonParser.parseReader(new FileReader(f.getPath()));
            //{
            //    "data":[{"id":"","power-core":"", "power-gpu":"", "power-other":"", "power-ram":""}]
            //}
            JsonArray dataArray = jsonTree.get( "data" ).getAsJsonArray();  // reading one file
            Double[][] energyData = StreamSupport.stream(
                    dataArray.spliterator(), true ).map(data -> {
                JsonObject entry = data.getAsJsonObject();
                return
                        new Double[]{
                                entry.get("power-core").getAsDouble(),
                                entry.get("power-gpu").getAsDouble(),
                                entry.get("power-other" ).getAsDouble(),
                                entry.get("power-ram" ).getAsDouble(),
                                entry.get("power-core").getAsDouble()
                                        + entry.get("power-gpu").getAsDouble()
                                        + entry.get("power-other" ).getAsDouble()
                                        + entry.get("power-ram" ).getAsDouble()
                        };
            } ).toArray( Double[][]::new );
            // TODO: parse duration
            energyData = transpose().apply( energyData );
            model.getMethodEnergyStatistics().computeIfAbsent( method, psiMethod -> new MethodEnergyStatistics(recorded, method) );
            model.getMethodEnergyStatistics().get( method ).addSample( 0l,energyData[0],energyData[1], energyData[2], energyData[3] );
        } catch( IOException e) {
            logger.error( e );
        }
    }

    @Override
    public boolean isManaProject() {
      VirtualFile manaProjectFiles = LocalFileSystem.getInstance().refreshAndFindFileByPath( project.getBasePath() + File.separator + MANA_NAME_SUFFIX );
      return manaProjectFiles != null && manaProjectFiles.exists();
    }

    @Override
    public List<VirtualFile> findAvailableManaFiles() {
        VirtualFile[] manaProjectFiles = LocalFileSystem.getInstance().refreshAndFindFileByPath( project.getBasePath() + File.separator + MANA_NAME_SUFFIX ).getChildren();
        // TODO - improve search for mana files.
        if( manaProjectFiles != null ) {
            return Arrays.stream(manaProjectFiles).filter( f -> f.getName().endsWith(MANA_NAME_SUFFIX) ).collect( Collectors.toList() );
        }
        return null;
    }

    /**
     * Initially we search for all available .mana-files - not sure, if this can lead to problems with a lot of files.
     */
    @Override
    public void init() {
        energyStatsModel = new HashMap<>();
        Double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        VirtualFile[] manaProjectFiles = LocalFileSystem.getInstance().refreshAndFindFileByPath( project.getBasePath() + File.separator + MANA_NAME_SUFFIX ).getChildren();
        for( VirtualFile folder : manaProjectFiles ) {
            if( folder.isDirectory() ) {
                try {
                    LocalDateTime recorded = FOLDER_DATE.parse(folder.getName()).toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    for (VirtualFile file : folder.getChildren()) {
                        if (!file.getName().endsWith(MANA_NAME_SUFFIX)) continue;

                        String className = file.getName().substring(0, file.getName().lastIndexOf('.')).replace("_", ".");
                        PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(className.substring(0,className.lastIndexOf('.')), GlobalSearchScope.projectScope(project));
                        String methodName = file.getName().substring( file.getName().indexOf( '_' ), file.getName().lastIndexOf('.') ).substring(1);
                        if (clazz != null) {
                            PsiMethod method = Arrays.stream(clazz.getMethods()).filter( psiMethod -> psiMethod.getName().equals( methodName ) ).findFirst().get();
                            energyStatsModel.computeIfAbsent(clazz, c -> new ManaEnergyExperimentModel());
                            ManaEnergyExperimentModel model = this.energyStatsModel.get(clazz);
                            computeStatistics(model, recorded, method, file);
                        }
                    }
                } catch( ParseException e ) {
                    logger.warn( "Could process folder " + folder.getName(), e );
                }
            }
        }

/*      final double range = max - min;
        final double minV = min;
        energyStatsModel.getMethodEnergyStatistics().forEach( v -> {
            if( ((v.getTotal() - minV )/ range) < 0.2 )
                v.setHeatColor( new JBColor(new Color(134, 252, 43, 60), new Color(134, 252, 43, 60)) );
            else if ( ((v.getTotal() - minV )/ range) < 0.4 )
                v.setHeatColor( new JBColor(new Color(252, 249, 43, 60), new Color(252, 249, 43, 60)) );
            else if ( ((v.getTotal() - minV )/ range) < 0.6 )
                v.setHeatColor( new JBColor(new Color(252, 147, 43, 60), new Color(252, 147, 43, 60)) );
            else if ( ((v.getTotal() - minV )/ range) < 0.8 )
                v.setHeatColor( new JBColor(new Color(255, 147, 43, 60), new Color(255, 147, 43, 60)) );
            else if ( ((v.getTotal() - minV )/ range) <= 1.0 )
                v.setHeatColor( new JBColor(new Color(252, 43, 43, 60), new Color(252, 43, 43, 60)) );
        } );
 */
    }



}
