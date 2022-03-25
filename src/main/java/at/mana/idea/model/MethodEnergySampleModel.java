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

/**
 * @author Andreas Schuler
 * @since 1.0
 */
@Getter
@Setter
public class MethodEnergySampleModel {
    
    private double duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double energyConsumption;
    private Double[] cpu;
    private Double[] gpu;
    private Double[] ram;
    private Double[] other;

    public MethodEnergySampleModel(double duration, Double[] cpu, Double[] gpu, Double[] ram, Double[] other, LocalDateTime startTime, LocalDateTime endTime) {
        this.duration = duration;
        this.cpu = cpu;
        this.gpu = gpu;
        this.ram = ram;
        this.other = other;
        this.startTime = startTime;
        this.endTime = endTime;
        this.energyConsumption = (this.duration /1000.0) * ( getCpuWattage().getAverage() + getGpuWattage().getAverage() + getRamWattage().getAverage() + getOtherWattage().getAverage() );
    }

    public DoubleStatistics getCpuWattage() {
        return Arrays.stream( cpu ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getGpuWattage() {
        return Arrays.stream( gpu ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getRamWattage() {
        return Arrays.stream( ram ).collect( DoubleStatistics.collector() );
    }

    public DoubleStatistics getOtherWattage() {
        return Arrays.stream( other ).collect( DoubleStatistics.collector() );
    }
}
