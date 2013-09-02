package org.almuallim.service.browser;

import java.beans.PropertyChangeListener;
import org.almuallim.service.url.AlmuallimURL;

/**
 * This interface is for opening
 * <code>AlmuallimURL</code> in a browser window. The browser window can also be
 * opened by just exposing the
 * <code>AlmuallimURL</code> in the lookup. But i was unable to do it from the
 * an action class.. In those scenarios where exposing AlmuallimURL is not an
 * option, using this interface to get a new instance of browser. apart from the
 * string property
 *
 * @author Naveed
 */
public interface Browser {

    public static String PROP_TITLE_CHANGED = "TITLE_CHANGED";
    public static String PAGE_LOADED = "PAGE_LOADED";

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void navigate(AlmuallimURL url);

    /**
     * Hook up to the propertychangelistener and wait for PAGE_LOADED property to
     * fire to make sure the DOM is ready before using the js
     *
     * @return JSEngine
     */
    public JSEngine getJSEngine();
}
