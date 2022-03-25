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
import java.util.HashSet;
import java.util.Set;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Measurement  {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne( fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private MemberDescriptor descriptor;

    @OneToMany( fetch = FetchType.LAZY, cascade =  CascadeType.ALL, mappedBy = "measurement")
    private Set<Sample> samples = new HashSet<>();


}
