/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.service;

import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.model.MethodEnergyModel;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface StorageService {

    static StorageService getInstance(@NotNull Project project) {
        return project.getService( StorageService.class);
    }

    public ManaEnergyExperimentModel findDataFor(PsiJavaFile file );

    public MethodEnergyModel findDataFor(PsiMethod method, PsiJavaFile file);

    void processAndStore( List<String> measurements);

    void setSelectedMethod( PsiMethod method );

    void clearSelectedMethod(  );

    boolean hasSelectedMethod( PsiMethod method );
}
