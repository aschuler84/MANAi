/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.plot;

import com.intellij.ui.jcef.JBCefBrowser;

import javax.swing.*;

public abstract class PlotComponent<T> {

    protected JBCefBrowser browser = new JBCefBrowser();
    protected T model;

    public void setModel( T model ) {
        this.model = model;
        this.initBrowser();
    }

    protected abstract void initBrowser(  );



    public JComponent createComponent() {
        return browser.getComponent();
    }


}
