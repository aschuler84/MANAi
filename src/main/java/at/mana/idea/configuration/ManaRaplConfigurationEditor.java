/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.configuration;

import at.mana.idea.util.I18nUtil;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.fields.ExtendableTextComponent;
import com.intellij.ui.components.fields.ExtendableTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;

public class ManaRaplConfigurationEditor extends SettingsEditor<ManaRaplJarConfiguration> implements DumbAware {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JSpinner spinnerSamplesRecorded;
    private JSlider slideroNoSamples;
    private JTextField txtNoSamples;
    private ExtendableTextField txtClass;
    private ComboBox<String> cmbMember;
    private ExtendableTextComponent.Extension browseExtension;

    private static final String CLASS_KEY = "configuration.ui.class.title";
    private static final String PROJECT_KEY = "configuration.ui.project.title";

    private final Project project;
    private PsiClass selectedClass;

    private static final String[] cmbContent = new String[]{
            I18nUtil.LITERALS.getString( PROJECT_KEY ),
            I18nUtil.LITERALS.getString( CLASS_KEY )
    };

    private static String projectName;

    public ManaRaplConfigurationEditor( Project project ) {
        this.project = project;
        slideroNoSamples.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                txtNoSamples.setText( slideroNoSamples.getValue() + "" );
            }
        });
        cmbMember.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if( e.getStateChange() == ItemEvent.SELECTED ){
                    String item = (String) e.getItem();
                    if( I18nUtil.LITERALS.getString(PROJECT_KEY).equals( item ) ) {
                        txtClass.removeExtension( getBrowseExtension() );
                        txtClass.setText( projectName );
                        txtClass.setToolTipText( "" );
                        txtClass.setEditable(false);
                    } else {
                        txtClass.addExtension( getBrowseExtension() );
                        txtClass.setText("");
                        txtClass.setToolTipText( "Specify class to record energy data for" );
                        txtClass.setEditable(true);
                    }
                    ComponentValidator.getInstance(txtClass).ifPresent( ComponentValidator::revalidate );
                }
            }
        });

        txtClass.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                ComponentValidator.getInstance(txtClass).ifPresent(ComponentValidator::revalidate);
            }
        });
    }

    @Override
    protected void resetEditorFrom(@NotNull ManaRaplJarConfiguration s) {
        projectName = s.getProject().getName();
        this.slideroNoSamples.setValue( s.getSamplingRate() );
        this.txtNoSamples.setText( s.getSamplingRate() + "" );
        this.spinnerSamplesRecorded.setValue( s.getNoOfSamples() );
        if( s.getSelectedClass() != null ) {
            this.cmbMember.setSelectedItem(  I18nUtil.LITERALS.getString(CLASS_KEY) );
            this.selectedClass = JavaPsiFacade.getInstance(project).findClass(s.getSelectedClass().getQualifiedName(), GlobalSearchScope.projectScope(project));
        } else {
            this.cmbMember.setSelectedItem(  I18nUtil.LITERALS.getString(PROJECT_KEY) );
            this.txtClass.setText( projectName );
        }
    }

    @Override
    protected void applyEditorTo(@NotNull ManaRaplJarConfiguration s) throws ConfigurationException {
        s.setNoOfSamples( (int) this.spinnerSamplesRecorded.getValue() ) ;
        s.setSamplingRate( this.slideroNoSamples.getValue() );
        if( I18nUtil.LITERALS.getString(CLASS_KEY).equals( this.cmbMember.getSelectedItem() ) ){
            if( selectedClass == null ) {
                ComponentValidator.getInstance(txtClass).ifPresent( ComponentValidator::revalidate );
                throw new ConfigurationException( "Specify a valid fully qualified class name." );
            }
            s.setSelectedClass( selectedClass );
        }

    }

    @Override
    protected @NotNull JComponent createEditor() {
        return this.panel1;
    }

    // Custom component create
    private void createUIComponents() {
       txtClass = new ExtendableTextField();
        new ComponentValidator(project).withValidator(() -> {

            if(StringUtil.isEmptyOrSpaces( txtClass.getText() ) ) {
                return new ValidationInfo( "Specify a valid fully qualified class name.", txtClass );
            }

            if( I18nUtil.LITERALS.getString(CLASS_KEY).equals( cmbMember.getSelectedItem() ) ) {
                PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(txtClass.getText(), GlobalSearchScope.projectScope(project));
                return psiClass != null ? null: new ValidationInfo(String.format("Class %s could not be found, specify a valid class.", txtClass.getText()), txtClass);
            }

            return null;
        }).installOn(txtClass);



       cmbMember = new ComboBox<String>();
       cmbMember.setModel( new DefaultComboBoxModel<String>( cmbContent ) );
       cmbMember.setEditable(false);
       cmbMember.setSelectedIndex(0);

    }

    private ExtendableTextComponent.Extension getBrowseExtension() {
        if( this.browseExtension == null )
            this.browseExtension =
                ExtendableTextComponent.Extension.create(AllIcons.Actions.ListFiles, AllIcons.Actions.ListFiles,
                        "Select class", () -> {
                            TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project)
                                    .createProjectScopeChooser("Select a test class", null);
                            chooser.showDialog();
                            selectedClass = chooser.getSelected();
                            txtClass.setText( selectedClass.getQualifiedName() );
                            ComponentValidator.getInstance(txtClass).ifPresent( ComponentValidator::revalidate );
                        });
        return this.browseExtension;
    }





}
