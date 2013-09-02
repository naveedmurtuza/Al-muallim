package org.almuallim.browser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.almuallim.service.browser.Browser;
import org.almuallim.service.browser.JSEngine;
import org.almuallim.service.url.AlmuallimURL;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = Browser.class)
public class JavaFxBrowser implements Browser, PropertyChangeListener {
    private BrowserTopComponent cetc;
    
    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void navigate(AlmuallimURL url) {
        cetc = new BrowserTopComponent();
        cetc.setUrl(url);
        cetc.open();
        cetc.requestActive();
        cetc.addPropertyChangeListener(PAGE_LOADED, this);
    }

    @Override
    public JSEngine getJSEngine() {
        return cetc.getJSEngine();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //ok the dom is ready
        propertyChangeSupport.firePropertyChange(PAGE_LOADED, null, PAGE_LOADED);
    }
}
