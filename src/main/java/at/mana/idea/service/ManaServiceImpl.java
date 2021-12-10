package at.mana.idea.service;

import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.model.MethodEnergyModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static at.mana.core.util.MatrixHelper.transposeDbl;

@Service
public class ManaServiceImpl implements ManaService {

    private final static String MANA_NAME_SUFFIX = ".mana";
    private VirtualFile selectedManaTraceFile;
    private final Project project;
    private Map<PsiClass,ManaEnergyExperimentModel> energyStatsModel = new HashMap<>();

    public static final SimpleDateFormat FOLDER_DATE = new SimpleDateFormat("ddMMyyyyHHmmss");
    private static final Logger logger = Logger.getInstance( ManaServiceImpl.class );

    public ManaServiceImpl(Project project) {
        this.project = project;
    }

    @Override
    public void before(@NotNull List<? extends VFileEvent> events) {
        ManaService.super.before(events);
    }

    @Override  // executed whenever a file is changed
    public void after(@NotNull List<? extends VFileEvent> events) {
        for( VFileEvent event : events ) {
            if( event.getFile() != null &&
                    event.getFile().getParent().getName().endsWith( MANA_NAME_SUFFIX ) ) {
                // search if a mana file has changed - if so overwrite the stats
                if( isValidManaFile( event.getFile() ) ) {
                    VirtualFile folder = event.getFile().getParent();
                    try {
                        LocalDateTime recorded = FOLDER_DATE.parse(folder.getName()).toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();

                        // whenever a file is changed -> look at the currently opened editor
                        // if the currently open class has new data available - parse it and

                        String className = event.getFile().getName().substring(0, event.getFile().getName().lastIndexOf('_')).replace("_", ".");
                        PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.projectScope(project));
                        String methodName = event.getFile().getName().substring( event.getFile().getName().indexOf( '_' ), event.getFile().getName().lastIndexOf('_') );

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

    private void computeStatistics(ManaEnergyExperimentModel model, LocalDateTime recorded, PsiMethod method, VirtualFile f) {
        try {
            JsonObject jsonTree = (JsonObject) JsonParser.parseReader(new FileReader(f.getPath()));
            JsonArray dataArray = jsonTree.get( "data" ).getAsJsonArray();// reading one file
            double duration = jsonTree.get("duration").getAsDouble();
            Double[][] energyData = StreamSupport.stream(
                    dataArray.spliterator(), true ).map(data -> {
                JsonObject entry = data.getAsJsonObject();
                return
                        new double[]{
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
            energyData = transposeDbl().apply( energyData );
            model.getMethodEnergyStatistics().computeIfAbsent( method, p -> new ArrayList<MethodEnergyModel>() );

            // try to find a method stats that fits the current date
            MethodEnergyModel methodEnergyModel = model.getMethodEnergyStatistics().get(method)
                    .stream().filter( m -> m.getStartDateTime().equals( recorded ) ).findFirst().orElseGet( () -> {
                        var m = new MethodEnergyModel(  );
                        model.getMethodEnergyStatistics().get( method ).add( m );
                        return m;
                    } );
            methodEnergyModel.addSample( (long) duration, energyData[0],energyData[1], energyData[2], energyData[3], null, null );
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
                        String className = file.getName().substring(0, file.getName().lastIndexOf('_')).replace("_", ".");
                        PsiClass clazz = JavaPsiFacade.getInstance(project).findClass(className.substring(0,className.lastIndexOf('.')), GlobalSearchScope.projectScope(project));
                        String methodName = file.getName().substring( file.getName().indexOf( '_' ), file.getName().lastIndexOf('_') ).substring(1);
                        if (clazz != null) {
                            PsiMethod method = Arrays.stream(clazz.getMethods()).filter( psiMethod -> psiMethod.getName().equals( methodName ) ).findFirst().get();
                            energyStatsModel.computeIfAbsent(clazz, c -> new ManaEnergyExperimentModel());
                            ManaEnergyExperimentModel model = this.energyStatsModel.get(clazz);
                            computeStatistics(model, recorded, method, file);
                        }
                    }
                } catch( ParseException e ) {
                    logger.warn( "Could not process folder " + folder.getName(), e );
                }
            }
        }
        notifyEnergyModelChanged(project);
    }

    private void notifyEnergyModelChanged( Project project ) {
        ManaEnergyDataNotifier publisher = project.getMessageBus().syncPublisher(ManaEnergyDataNotifier.MANA_ENERGY_DATA_NOTIFIER_TOPIC);
        publisher.update( new EnergyDataNotifierEvent( project, null ));
    }


}
