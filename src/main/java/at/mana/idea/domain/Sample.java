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
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    @GeneratedValue( generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
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
    private Set<Trace> trace = new HashSet<>();


}
