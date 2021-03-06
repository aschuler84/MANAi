/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.model;

import at.mana.idea.domain.ClassEnergyStatistics;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaEditorModel  {

    private final VirtualFile source;

    public ManaEditorModel(VirtualFile source) {
        this.source = source;
    }

    public List<ClassEnergyStatistics> getClassEnergyStatistics(){
        return List.of(
                ClassEnergyStatistics.builder().className("TestClassA").packageName("com.google.gson").build(),
                ClassEnergyStatistics.builder().className("TestClassB").packageName("com.google.gson").build(),
                ClassEnergyStatistics.builder().className("TestClassC").packageName("com.google.gson").build(),
                ClassEnergyStatistics.builder().className("TestClassD").packageName("com.google.gson").build(),
                ClassEnergyStatistics.builder().className("TestClassE").packageName("com.google.gson").build(),
                ClassEnergyStatistics.builder().className("TestClassF").packageName("com.google.gson").build(),
                ClassEnergyStatistics.builder().className( "TestClassG" ).packageName("com.google.gson").build(),
                ClassEnergyStatistics.builder().className( "TestClassH" ).packageName("com.google.gson").build() );
    }

    public List<ClassEnergyStatistics> getTopXClassEnergyStatistics( int limit ) {
        return this.getClassEnergyStatistics()
                .stream().sorted( (o1, o2) -> (int) (o1.getEnergyConsumption() - o2.getEnergyConsumption()) )
                .limit(limit).collect(Collectors.toList());
    }

}
