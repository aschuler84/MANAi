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
