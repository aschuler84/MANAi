/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea;

import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.service.StorageService;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Collection;


/**
 * @author Andreas Schuler
 * @since 1.0
 */
public class ManaMethodMarkerProvider extends RelatedItemLineMarkerProvider implements UpdateInBackground {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        StorageService service =StorageService.getInstance(project);
        // This must be an element with a literal expression as a parent
        if ( !( element.getParent() instanceof PsiMethod && element instanceof PsiIdentifier )
            ||  !(element.getContainingFile() instanceof PsiJavaFile)) {
            return;
        }

        PsiMethod method = (PsiMethod) element.getParent();
        MethodEnergyModel statistics =  ReadAction.compute( () ->  service.findDataFor( method, (PsiJavaFile) element.getContainingFile() ) );
        if( statistics == null ) {
            return;
        }
        NavigationGutterIconBuilder<PsiElement> builder =
                NavigationGutterIconBuilder.create( Icons.LOGO_EMPTY_BG_GUTTER )
                        .setTargets( element )
                        .setTooltipText("MANAi energy profile");
        result.add(builder.createLineMarkerInfo(element));
    }
}
