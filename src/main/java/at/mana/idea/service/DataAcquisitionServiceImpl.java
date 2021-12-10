package at.mana.idea.service;

import at.mana.core.util.StringUtil;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataAcquisitionServiceImpl extends ProcessAdapter implements DataAcquisitionService {

    private static final Logger logger = Logger.getInstance( DataAcquisitionServiceImpl.class );
    private BackgroundableProcessIndicator indicator;

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if( indicator != null ) {
            indicator.stop();
        }
    }

    @Override
    public void startDataAcquisition( @NotNull Project project ) {
        if( indicator != null && indicator.isRunning() ) {
            throw new RuntimeException( "Only one RAPL Measurement allowed at the same time." );
        }
        var task = new Task.Backgroundable( project, "Mana Acquire Data" ){
            @SneakyThrows
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ServerSocket serverSocket = new ServerSocket(9999);  // TODO: make port configurable
                List<String> measurements = new ArrayList<>();
                int read;
                while(!indicator.isCanceled() && indicator.isRunning() ) {
                    StringBuilder builder = new StringBuilder();
                    serverSocket.setSoTimeout(10000);
                    try {
                        indicator.setText( "Waiting for client connection..." );
                        Socket socket = serverSocket.accept();
                        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        while ((read = socketReader.read()) != -1 && read != '\0') {
                            builder.append((char) read);
                        }
                        socketReader.close();
                        socket.close();
                        logger.debug(StringUtil.coloredString().yellow().withText("data received: ").build() + builder.toString());
                        indicator.setText( "Receiving data ..." );
                        measurements.add( builder.toString() );
                    } catch (SocketTimeoutException e) {
                        // no client acquired connection - trying again
                        logger.debug( "No client available, trying again..." );
                    }
                }
                StorageService service = StorageService.getInstance(project);
                service.processAndStore( measurements );
                ManaEnergyDataNotifier publisher = project.getMessageBus()
                        .syncPublisher(ManaEnergyDataNotifier.MANA_ENERGY_DATA_NOTIFIER_TOPIC);
                publisher.update( new EnergyDataNotifierEvent( project, null ));
            }

        };
        indicator = new BackgroundableProcessIndicator( project, task );
        ProgressManager.getInstance().runProcessWithProgressAsynchronously( task, indicator );
    }


}