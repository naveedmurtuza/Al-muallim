/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.lucene.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import javafx.application.Platform;
import org.almuallim.service.browser.Browser;
import org.almuallim.service.browser.JSEngine;
import org.almuallim.service.search.SearchCallback;
import org.almuallim.service.search.SearchResult;
import org.almuallim.service.url.AlmuallimURL;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Window",
        id = "org.almuallim.lucene.search.SearchAction")
@ActionRegistration(
        displayName = "#CTL_SearchAction")
@ActionReference(path = "Menu/Window", position = -90)
@Messages("CTL_SearchAction=Search")
public final class SearchAction implements ActionListener {

    private Browser browser;

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
        browser = Lookup.getDefault().lookup(Browser.class);
        String url = "almuallim://Search?" + AlmuallimURL.ClassNameKey + "=org.almuallim.lucene.search.SearchUrlOpener";
        try {
            browser.navigate(new AlmuallimURL(url));
            // browser.addPropertyChangeListener(this);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
