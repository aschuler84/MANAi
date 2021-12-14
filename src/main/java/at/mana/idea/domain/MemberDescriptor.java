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
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class MemberDescriptor {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String hash;
    private String methodName;
    private String methodDesc;
    private String className;

    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "descriptor")
    private Set<Measurement> measurements = new HashSet<>();

    public MemberDescriptor() {

    }

    public MemberDescriptor( String hash, String methodName, String methodDesc, String className ) {
        this.hash = hash;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.className = className;
    }



}
