/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Sample {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Measurement measurement;
    private Long duration;

    private Long samplingPeriod;

    @ElementCollection
    private List<Double> powerCore;
    @ElementCollection
    private List<Double> powerGpu;
    @ElementCollection
    private List<Double> powerRam;
    @ElementCollection
    private List<Double> powerOther;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,mappedBy = "sample")
    private List<Trace> trace;


}
