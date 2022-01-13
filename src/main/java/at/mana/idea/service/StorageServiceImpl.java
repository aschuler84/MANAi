/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.service;

import at.mana.core.util.HashUtil;
import at.mana.core.util.KeyValuePair;
import at.mana.idea.domain.Measurement;
import at.mana.idea.domain.MemberDescriptor;
import at.mana.idea.domain.Sample;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.model.MethodEnergySampleModel;
import at.mana.idea.util.DateUtil;
import at.mana.idea.util.HibernateUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.components.Service;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.ClassUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static at.mana.core.util.MatrixHelper.transposeDbl;

@Service
public class StorageServiceImpl implements StorageService {

    private final Map<PsiJavaFile, ManaEnergyExperimentModel> model = new HashMap<>();


    @Override
    public ManaEnergyExperimentModel findDataFor(PsiJavaFile file) {
        if( model.get(file) == null ) {
            // data is invalidated once new records are created
            ManaEnergyExperimentModel eModel = model.computeIfAbsent( file, f -> new ManaEnergyExperimentModel() );
            eModel.setExperimentFile(file);
            PsiClass[] classes = file.getClasses();
            List<KeyValuePair<PsiMethod, String>> keys = Arrays.stream(classes).flatMap(c -> Arrays.stream(c.getMethods()))
                    .map(m -> new KeyValuePair<>(m, HashUtil.hash(
                            m.getName(),
                            ClassUtil.getJVMClassName(m.getContainingClass()),
                            ClassUtil.getAsmMethodSignature(m))))
                    .collect(Collectors.toList());

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
            Map<String, List<JsonObject>> groupedEntries = parseRecordedData( recorded );
            HibernateUtil.executeInTransaction(session -> {
                for( var entry : groupedEntries.entrySet() ) {
                    Measurement measurement = new Measurement();
                    entry.getValue().stream().forEach( json -> {
                        JsonArray dataArray = json.get("data").getAsJsonArray();
                        String hash = json.get("hash").getAsString();
                        String method = json.get("methodName").getAsString();
                        String clazz = json.get("className").getAsString();
                        String methodDescriptor = json.get("methodDescriptor").getAsString();
                        LocalDateTime startDateTime = LocalDateTime.parse( json.get("startTime").getAsString(), DateUtil.Formatter);
                        LocalDateTime endDateTime = LocalDateTime.parse( json.get("endTime").getAsString(), DateUtil.Formatter);
                        long duration = json.get("duration").getAsLong();
                        long samplingRate = json.get("samplingRate").getAsLong();

                        // try to find method descriptor in database get descriptor if available otherwise get new one
                        MemberDescriptor descriptor = findOrDefault(hash, new MemberDescriptor(
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
                        energyData = transposeDbl().apply(energyData);
                        Sample sample = new Sample();
                        sample.setStartDateTime( startDateTime );
                        sample.setEndDateTime( endDateTime );
                        sample.setDuration( duration );

                        sample.setDuration(duration);
                        sample.setPowerCore(Arrays.asList(energyData[0]));
                        sample.setPowerGpu(Arrays.asList(energyData[1]));
                        sample.setPowerRam(Arrays.asList(energyData[2]));
                        sample.setPowerOther(Arrays.asList(energyData[3]));
                        sample.setMeasurement(measurement);

                        measurement.getSamples().add( sample );
                        measurement.setDescriptor(descriptor);
                        descriptor.getMeasurements().add(measurement);
                        session.save(descriptor);
                    } );
                }
                return null;
            });
            invalidateModel(); // invalidate after data is committed
        }
    }

    private Map<String, List<JsonObject>> parseRecordedData(List<String> recorded) {
        return recorded.stream().map( e -> (JsonObject) JsonParser.parseString(e) )
                .collect( Collectors.groupingBy( json -> json.get("hash").getAsString() ) );
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

    private MemberDescriptor findOrDefault(String hash, @Nullable  MemberDescriptor memberDescriptor) {
        return HibernateUtil.executeInTransaction(session -> {
            var builder = session.getCriteriaBuilder();
            var query = builder.createQuery(MemberDescriptor.class);
            var root = query.from(MemberDescriptor.class);
            query = query.select(root).where(root.get("hash").in(hash));
            var result = session.createQuery(query).getResultList();
            return result.size() == 1 ? result.get(0) : memberDescriptor;
        });
    }

}

