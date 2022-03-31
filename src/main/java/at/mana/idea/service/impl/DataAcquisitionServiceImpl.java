/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.service.impl;

import at.mana.core.util.StringUtil;
import at.mana.idea.service.DataAcquisitionService;
import at.mana.idea.service.EnergyDataNotifierEvent;
import at.mana.idea.service.ManaEnergyDataNotifier;
import at.mana.idea.service.StorageService;
import static at.mana.idea.util.I18nUtil.i18n;
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

/**
 * @author Andreas Schuler
 * @since 1.0
 */
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
            throw new RuntimeException(i18n("dataacquisition.mana.exception"));
        }
        var task = new Task.Backgroundable( project, i18n("dataacquisition.mana.title") ){
            @SneakyThrows
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try(ServerSocket serverSocket = new ServerSocket(9999) ) { // TODO: make port configurable
                    List<String> measurements = new ArrayList<>();
                    int read;
                    while(!indicator.isCanceled() && indicator.isRunning() ) {
                        StringBuilder builder = new StringBuilder();
                        serverSocket.setSoTimeout(10000);
                        try {
                            indicator.setText( i18n("dataacquisition.mana.status.waiting") );
                            Socket socket = serverSocket.accept();
                            BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            while ((read = socketReader.read()) != -1 && read != '\0') {
                                builder.append((char) read);
                            }
                            socketReader.close();
                            socket.close();
                            logger.debug(StringUtil.coloredString().yellow().withText("data received: ").build() + builder.toString());
                            indicator.setText( i18n("dataacquisition.mana.status.received") );
                            measurements.add( builder.toString() );
                        } catch (SocketTimeoutException ignore) {
                            // exception is ignored - required to verify if process is still running
                        }
                    }
                    StorageService service = StorageService.getInstance(project);
                    service.processAndStore( measurements );
                    ManaEnergyDataNotifier publisher = project.getMessageBus()
                            .syncPublisher(ManaEnergyDataNotifier.MANA_ENERGY_DATA_NOTIFIER_TOPIC);
                    publisher.update( new EnergyDataNotifierEvent( project, null ));
                }
            }

        };
        indicator = new BackgroundableProcessIndicator( project, task );
        ProgressManager.getInstance().runProcessWithProgressAsynchronously( task, indicator );
    }


}
