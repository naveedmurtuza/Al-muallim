/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.service.browser;

/**
 *
 * @author Naveed
 */
public class PageObject {

    private String selectedText;
    private SelectedObject selectedObject;

    public PageObject(String selectedText, SelectedObject selectedObject) {
        this.selectedText = selectedText;
        this.selectedObject = selectedObject;
    }

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    public SelectedObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(SelectedObject selectedObject) {
        this.selectedObject = selectedObject;
    }
}
