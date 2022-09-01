/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.service.impl;

import static at.mana.core.util.MatrixHelper.transposeDbl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import at.mana.idea.domain.*;
import at.mana.idea.service.MemberDescriptorService;
import at.mana.idea.service.StorageService;
import at.mana.idea.service.TraceService;
import at.mana.idea.util.MethodUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import at.mana.core.util.KeyValuePair;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.model.MethodEnergySampleModel;
import at.mana.idea.util.DateUtil;
import at.mana.idea.util.HibernateUtil;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
@Service
public class StorageServiceImpl implements StorageService
{

    private final Map<PsiJavaFile, ManaEnergyExperimentModel> model = new HashMap<>();
    private Project project;

    private PsiMethod selectedMethod;

    private MemberDescriptorService memberDescriptorService;
    private TraceService traceService;

    public StorageServiceImpl(Project project) {
        this.project = project;
        this.traceService = project.getService( TraceService.class );
        this.memberDescriptorService = project.getService(MemberDescriptorService.class);
        initFileChangeListener();
    }

    private void initFileChangeListener() {
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES,
            new BulkFileListener() {
                @Override
                public void after(@NotNull List<? extends VFileEvent> events) {
                    for( VFileEvent event : events ) {
                        VirtualFile file = event.getFile();
                        if( file != null && file.isValid() ) {  // #28: verify that file is valid before proceeding
                            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                            if (psiFile instanceof PsiJavaFile
                                    && !psiFile.getFileType().getDefaultExtension().endsWith("class")) {
                                PsiJavaFile javaFile = (PsiJavaFile) psiFile;
                                model.remove(javaFile);
                            }
                        }
                    }
                }
        });
    }

    @Override
    public ManaEnergyExperimentModel findDataFor(PsiJavaFile file) {

        if( DumbService.isDumb( file.getProject() ) )
            return null;  // return null when indices are being built

        if( model.get(file) == null ) {

            // data is invalidated once new records are created
            ManaEnergyExperimentModel eModel = model.computeIfAbsent( file, f -> new ManaEnergyExperimentModel() );
            eModel.setExperimentFile(file);
            List<KeyValuePair<PsiMethod, String>> keys = MethodUtil.buildHashFromMethods(file);
            HibernateUtil.executeInTransaction(session -> {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<MemberDescriptor> query = builder.createQuery(MemberDescriptor.class);
                Root<MemberDescriptor> root = query.from(MemberDescriptor.class);
                query = query.select(root).where(root.get("hash").in(keys.stream().map(KeyValuePair::getValue).collect(Collectors.toList())));
                List<MemberDescriptor> result = session.createQuery(query).getResultList();

                for (MemberDescriptor memberDescriptor : result) {
                    Optional<KeyValuePair<PsiMethod, String>> matchOpt = keys.stream().filter(k -> k.getValue().equals(memberDescriptor.getHash())).findFirst();
                    if (matchOpt.isPresent()) {
                        PsiMethod matchingMethod = matchOpt.get().getKey();
                        eModel.getMethodEnergyStatistics().computeIfAbsent(matchingMethod, m -> new ArrayList<>());
                        var mModels = memberDescriptor.getMeasurements().stream()
                                .map(this::fromMeasurement).collect(Collectors.toList());
                        eModel.getMethodEnergyStatistics().get(matchingMethod).addAll(mModels);
                    }
                }
                return model;
            });
        }
        return model.get( file );
    }

    @Override
    public @Nullable MethodEnergyModel findDataFor(@NotNull PsiMethod method, PsiJavaFile file) {
        if( model.get(file) == null ) {
            findDataFor( file ); // try loading data if file currently not present
        }
        if( model.get(file) != null && model.get(file).getMethodEnergyStatistics().get(method) != null ) {
            Optional<MethodEnergyModel> m = model.get(file).getMethodEnergyStatistics().get(method)
                    .stream().min(Comparator.comparing(MethodEnergyModel::getStartDateTime));
            return m.orElse(null);
        }
        return null;
    }

    @Override
    public void processAndStore(List<String> recorded) {
        if (recorded != null && !recorded.isEmpty()) {
            Map<String, List<JsonObject>> groupedEntries = parseRecordedEnergyData( recorded );
            HibernateUtil.executeInTransaction(session -> {
                Run run = new Run();
                run.setDate( LocalDateTime.now() );
                for( var entry : groupedEntries.entrySet() ) {
                    Measurement measurement = new Measurement();
                    entry.getValue().stream().forEach( json -> {

                        JsonObject rootEntry = json.getAsJsonObject("energy");
                        JsonArray childEntries = json.getAsJsonArray("trace");

                        JsonArray dataArray = rootEntry.get("data").getAsJsonArray();
                        String hash = rootEntry.get("hash").getAsString();
                        String method = rootEntry.get("methodName").getAsString();
                        String clazz = rootEntry.get("className").getAsString();
                        String methodDescriptor = rootEntry.get("methodDescriptor").getAsString();
                        LocalDateTime startDateTime = LocalDateTime.parse( rootEntry.get("startTime").getAsString(), DateUtil.Formatter);
                        LocalDateTime endDateTime = LocalDateTime.parse( rootEntry.get("endTime").getAsString(), DateUtil.Formatter);
                        long duration = rootEntry.get("duration").getAsLong();
                        long samplingRate = rootEntry.get("samplingRate").getAsLong();

                        // try to find method descriptor in database get descriptor if available otherwise get new one
                        MemberDescriptor descriptor = memberDescriptorService.findOrDefault(hash, new MemberDescriptor(
                                hash, method, methodDescriptor, clazz
                        ));

                        Double[][] energyData = StreamSupport.stream(
                                dataArray.spliterator(), true).map(data -> {
                            JsonObject jsonEntry = data.getAsJsonObject();
                            return
                                    new Double[]{
                                            jsonEntry.get("powerCore").getAsDouble(),
                                            jsonEntry.get("powerGpu").getAsDouble(),
                                            jsonEntry.get("powerOther").getAsDouble(),
                                            jsonEntry.get("powerRam").getAsDouble(),
                                            jsonEntry.get("powerCore").getAsDouble()
                                                    + jsonEntry.get("powerGpu").getAsDouble()
                                                    + jsonEntry.get("powerOther").getAsDouble()
                                                    + jsonEntry.get("powerRam").getAsDouble()
                                    };
                        }).toArray(Double[][]::new);
                        if( energyData != null && energyData.length > 0  ) {
                            energyData = transposeDbl().apply(energyData);
                            Sample sample = new Sample();
                            sample.setStartDateTime(startDateTime);
                            sample.setEndDateTime(endDateTime);
                            sample.setDuration(duration);

                            sample.setDuration(duration);
                            sample.setSamplingPeriod( samplingRate );
                            sample.setPowerCore( new ArrayList<>(Arrays.asList(energyData[0])) );
                            sample.setPowerGpu( new ArrayList<>(Arrays.asList(energyData[1])) );
                            sample.setPowerRam( new ArrayList<>(Arrays.asList(energyData[2])) );
                            sample.setPowerOther( new ArrayList<>(Arrays.asList(energyData[3])) );
                            sample.setMeasurement(measurement);

                            cleanSamples( sample );

                            measurement.getSamples().add(sample);
                            measurement.setDescriptor(descriptor);
                            descriptor.getMeasurements().add(measurement);
                            run.getMeasurements().add( measurement );
                            session.save(measurement);
                            session.save(descriptor);
                            session.save(run);
                            traceService.attributeTraces( sample, rootEntry, childEntries );
                        }
                        session.flush();
                    } );

                }
                return null;
            });
            invalidateModel(); // invalidate after data is committed
        }
    }



    private void cleanSamples( Sample sample ){
        sample.getPowerCore().removeIf( v -> v.equals( Double.NaN ) || v.equals(Double.POSITIVE_INFINITY) || v.equals(Double.NEGATIVE_INFINITY) );
        sample.getPowerGpu().removeIf( v -> v.equals( Double.NaN ) || v.equals(Double.POSITIVE_INFINITY) || v.equals(Double.NEGATIVE_INFINITY));
        sample.getPowerOther().removeIf( v -> v.equals( Double.NaN ) || v.equals(Double.POSITIVE_INFINITY) || v.equals(Double.NEGATIVE_INFINITY) );
        sample.getPowerRam().removeIf( v -> v.equals( Double.NaN ) || v.equals(Double.POSITIVE_INFINITY) || v.equals(Double.NEGATIVE_INFINITY) );
    }

    private Map<String, List<JsonObject>> parseRecordedEnergyData(List<String> recorded) {
        // returns JsonObject(entry, trace)
        return recorded.stream().map( e -> JsonParser.parseString(e).getAsJsonObject() )
                .collect( Collectors.groupingBy( json ->
                        json.getAsJsonObject("energy").get("hash").getAsString() ) );
        //return recorded.stream().map( e -> ((JsonObject) JsonParser.parseString(e)).getAsJsonObject("energy") )
        //        .collect( Collectors.groupingBy( json -> json.get("hash").getAsString() ) );
    }

    private void invalidateModel(){
        this.model.clear();
    }

    private MethodEnergyModel fromMeasurement(Measurement measurement) {
        var model = new MethodEnergyModel();
        measurement.getSamples().forEach( sample -> {
            var mModel =  new MethodEnergySampleModel(
                    sample.getDuration(),
                    sample.getPowerCore().toArray(new Double[0]),
                    sample.getPowerGpu().toArray(new Double[0]),
                    sample.getPowerRam().toArray(new Double[0]),
                    sample.getPowerOther().toArray(new Double[0]),
                    sample.getStartDateTime(), sample.getEndDateTime());
            model.getSamples().add( mModel );
        });
        return model;
    }



}

