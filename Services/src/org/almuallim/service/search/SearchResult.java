package org.almuallim.service.search;

import java.util.HashMap;

public class SearchResult {

    private String text;
    private String uri;
    private String title;
    private String abstractTxt;
    private String moduleName;
    private HashMap<String, Object> fields = new HashMap<>();

    public SearchResult() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstractTxt() {
        return abstractTxt;
    }

    public void setAbstractTxt(String abstractTxt) {
        this.abstractTxt = abstractTxt;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void addField(String key, Object value) {
        fields.put(key, value);
    }

    public void removeField(String key) {
        fields.remove(key);
    }

    public HashMap<String, Object> getFields() {
        return fields;
    }
}
