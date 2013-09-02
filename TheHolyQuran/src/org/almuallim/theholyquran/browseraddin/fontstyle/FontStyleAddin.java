/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.browseraddin.fontstyle;

import java.awt.event.ActionEvent;
import java.util.EnumSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.almuallim.service.browser.BrowserAddIn;
import org.almuallim.service.browser.JSEngine;
import org.almuallim.service.browser.ActionDisplayPosition;
import org.almuallim.service.browser.ActionDisplayStyle;
import org.almuallim.theholyquran.ModuleConstants;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = BrowserAddIn.class, position = 1000)
public class FontStyleAddin implements BrowserAddIn {

    private final ImageIcon icon;
    private Document dom;
    private JSEngine engine;
    private WebView view;
    private Action action;

    public FontStyleAddin() {
        icon = new ImageIcon(getClass().getResource("colors.png"));
    }

    @Override
    public void init(Document dom, JSEngine engine, WebView view) {
        this.dom = dom;
        this.engine = engine;
        this.view = view;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //path home/[modulename]/fontstyles.css
                    final StringBuilder sb = new StringBuilder();
                    Preferences defaultStyles = NbPreferences.forModule(getClass()).node("styles/default");
                    for (String name : defaultStyles.childrenNames()) {
                        sb.append(getCssSyle(defaultStyles, name));//.append(System.lineSeparator());
                    }
                    Preferences translatorStyles = NbPreferences.forModule(getClass()).node("styles/translator");
                    for (String name : translatorStyles.childrenNames()) {
                        sb.append(getCssSyle(translatorStyles, name));//.append(System.lineSeparator());
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(sb.toString());
                            FontStyleAddin.this.engine.executeScript(String.format("insertCssClass(\"%s\");", sb.toString()));
                        }
                    });

                } catch (BackingStoreException bse) {
                    Exceptions.printStackTrace(bse);
                }
            }
        }).start();
    }

    private String getCssSyle(Preferences pref, String name) {
        Preferences node = pref.node(name);
        //build the css class name
        StringBuilder sb = new StringBuilder();
        sb.append(".").append("style_").append(name).append(" {");
        String prop;
        prop = node.get("font-family", "");
        if (!prop.isEmpty()) {
            sb.append("font-family: '").append(prop).append("';");
        }
        prop = node.get("font-size", "");
        if (!prop.isEmpty()) {
            sb.append("font-size: ").append(prop).append("px ;");
        }
        prop = node.get("color", "");
        if (!prop.isEmpty()) {
            sb.append("color: ").append(prop).append(" ;");
        }
        prop = node.get("background", "");
        if (!prop.isEmpty()) {
            sb.append("background-color: ").append(prop).append(" ;");
        }

//        prop = node.get("font-style", "");
//
//        if (!prop.isEmpty()) {
//            sb.append("font-style: ").append(prop).append(";");
//        }
        sb.append(" }");
        //add this style to dom
        return sb.toString();
    }

    @Override
    public Action getAction() {
        if (action == null) {
            action = new FontStyleAction();
        }
        return action;
    }

    @Override
    public EnumSet<ActionDisplayStyle> getDisplayStyle() {
        return EnumSet.of(ActionDisplayStyle.BOTH);
    }

    @Override
    public EnumSet<ActionDisplayPosition> getDisplayPosition() {
        return EnumSet.of(ActionDisplayPosition.TOOLBAR);
    }

    @Override
    public int getPosition() {
        return 1234;
    }

    @Override
    public boolean separatorAfter() {
        return false;
    }

    @Override
    public boolean separatorBefore() {
        return true;
    }

    @Override
    public String getSupportedModules() {
        return ModuleConstants.MODULE_NAME;
    }

    private class FontStyleAction extends AbstractAction {

        public FontStyleAction() {
            super("Font Style", icon);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            // read style properties 
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Object o = engine.executeScript("$('html').clone().html();");

//                           ((JSObject)o).eval(NAME);
                    int f = 1;

                }
            });

            FontStylePanel panel = new FontStylePanel();
            panel.init();
            DialogDescriptor dd = new DialogDescriptor(panel, "Select Translations");

            DialogDisplayer.getDefault().notify(dd);
            panel.save();


        }
    }
}