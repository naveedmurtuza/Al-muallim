package org.almuallim.service.browser;

/**
 * An interface that provides all the base Javascript files and css styles like
 * Jquery 1.9, JQuery UI, noty, qtip, sticky, toolbar and some custom JS
 * Functions For details see global.js
 *
 * @author Naveed Quadri
 */
public interface JavascriptFrameworkProvider {

    /**
     * Gets the all the framework files wrapped in their respective tags.
     * @return 
     */
    public String getFramework();

}
