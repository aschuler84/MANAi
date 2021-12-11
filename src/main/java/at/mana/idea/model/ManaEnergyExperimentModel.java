/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.model;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ManaEnergyExperimentModel {

    private PsiFile experimentFile;
    private Map<PsiMethod, List<MethodEnergyModel>> methodEnergyStatistics = new HashMap<>();

}
