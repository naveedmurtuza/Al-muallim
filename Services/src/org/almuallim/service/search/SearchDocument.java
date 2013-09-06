/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.service.search;

import java.util.Map;

/**
 *
 * @author Naveed
 */
public class SearchDocument {

    private final String text;
    private final String title;
    private final String url;
    private final String moduleName;
    private Map<String, Object> parameters;
    private String abstractText;

    /**
     *
     * @param text text to index
     * @param title title is stored as is.
     * @param url the url to this resource.
     * @param moduleName name of the module
     */
    public SearchDocument(String text, String title, String url, String moduleName) {
        this.text = text;
        this.title = title;
        this.url = url;
        this.moduleName = moduleName;
        abstractText = text.substring(0, Math.min(100, text.length()));
        if (text.length() > 100) {
            abstractText += " ...";
        }
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getAbstractText() {
        return abstractText;
    }

    /**
     * The parameters are stored as is in the index
     *
     * @param parameters
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
