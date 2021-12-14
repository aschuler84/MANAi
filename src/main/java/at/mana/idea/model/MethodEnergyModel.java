/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.model;

import at.mana.core.util.DoubleStatistics;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class MethodEnergyModel {

    private Set<MethodEnergySampleModel> samples = new HashSet<>();

    public MethodEnergyModel() {
    }

    public LocalDateTime getStartDateTime() {
        return this.getSamples() != null ? this.getSamples().stream().min(
                Comparator.comparing( MethodEnergySampleModel::getStartTime ) )
                .map( MethodEnergySampleModel::getStartTime ).orElse(null) : null;
    }

    public LocalDateTime getEndDateTime() {
        return this.getSamples() != null ? this.getSamples().stream().max(
                Comparator.comparing( MethodEnergySampleModel::getEndTime ) )
                .map( MethodEnergySampleModel::getStartTime ).orElse(null) : null;
    }

    public DoubleStatistics getCpuWattage() {
        return samples.stream()
            .flatMap( sample -> Arrays.stream( sample.getCpu() ) ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getGpuWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getGpu() ) ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getRamWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getRam() ) ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getOtherWattage() {
        return samples.stream()
                .flatMap( sample -> Arrays.stream( sample.getOther() ) ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getDuration() {
        return  samples.stream().map(MethodEnergySampleModel::getDuration).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getEnergyConsumption() {
        return samples.stream().map(MethodEnergySampleModel::getEnergyConsumption).collect( DoubleStatistics.collector() );
    }

    public void addSample( long durationMilliseconds, Double[] cpu, Double[] gpu, Double[] ram, Double[] other, LocalDateTime startDateTime, LocalDateTime endDateTime ) {
        this.samples.add( new MethodEnergySampleModel( durationMilliseconds / 1000.0, cpu, gpu, ram, other, startDateTime, endDateTime ) );
    }


    public Double getTotal() {
        return this.getCpuWattage().getAverage() + this.getGpuWattage().getAverage() + this.getOtherWattage().getAverage() + this.getRamWattage().getAverage();
    }

}
