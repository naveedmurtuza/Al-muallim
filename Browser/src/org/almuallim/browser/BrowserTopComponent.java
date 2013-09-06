/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.browser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javax.swing.Action;
import javax.swing.JPanel;
import netscape.javascript.JSObject;
import org.almuallim.service.browser.Browser;
import org.almuallim.service.browser.BrowserAddIn;
import org.almuallim.service.browser.JSEngine;
import org.almuallim.service.browser.ActionDisplayPosition;
import org.almuallim.service.helpers.JavaFX;
import org.almuallim.service.url.AlmuallimURL;
import org.almuallim.service.url.AlmuallimURLOpener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.almuallim.browser//Browser//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "BrowserTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "org.almuallim.browser.BrowserTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BrowserAction",
        preferredID = "BrowserTopComponent")
@Messages({
    "CTL_BrowserAction=Browser",
    "CTL_BrowserTopComponent=Browser Window",
    "HINT_BrowserTopComponent=This is a Browser window"
})
public final class BrowserTopComponent extends TopComponent implements PreferenceChangeListener {

    private static final Logger LOG = Logger.getLogger(BrowserTopComponent.class.getName());
    private JFXPanel browserFxPanel;
    private WebEngine engine;
    private AlmuallimURL url;
    private Collection<? extends BrowserAddIn> browserAddins;
    private boolean padeLoaded;
    private int scroll;
    private JSObject jsWindowObject;
    private boolean resumingState;
    private WebView view;
    private JSEngine jSEngine;

    public BrowserTopComponent() {
        initComponents();
        setName(Bundle.CTL_BrowserTopComponent());
        initFx();
        init();
        NbPreferences.forModule(Browser.class).addPreferenceChangeListener(this);
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                browserAddins  = Lookup.getDefault().lookupAll(BrowserAddIn.class);
            }
        }).start();
    }

    public AlmuallimURL getUrl() {
        return url;
    }

    public void setUrl(AlmuallimURL url) {
        this.url = url;
        loadUrl();
    }

    public JSEngine getJSEngine() {
        if (jSEngine == null) {
            jSEngine = new JSEngine() {
                @Override
                public Object executeScript(String script) {
                    return engine.executeScript(script);
                }

                @Override
                public void registerJavaFunction(String name, Object member) {
                    jsWindowObject.setMember(name, member);
                }
            };
        }
        return jSEngine;
    }

    private void loadUrl() {
        LOG.fine("Resolving openers");
        String className = url.getParameters().get(AlmuallimURL.ClassNameKey);
        AlmuallimURLOpener urlOpener = AlmuallimURLOpener.Resolver.resolve(className);
        if (urlOpener == null) {
            LOG.log(Level.WARNING, "No openers found for {0}", className);
            return;
        }
        try {
            final File temp = File.createTempFile("almuallim_", ".html");
            urlOpener.generateHtml(url, temp);
            LOG.info("loading engine ....");
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    engine.load("file:///" + temp.getAbsolutePath());

                }
            });
            setName(url.getParameters().containsKey("title") ? url.getParameters().get("title") : url.getModuleName());
            LOG.info(temp.getAbsoluteFile().getAbsolutePath());
            //is it good? or shud be deleted when the window closes?
            temp.deleteOnExit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new JXToolbar();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setToolTipText(org.openide.util.NbBundle.getMessage(BrowserTopComponent.class, "BrowserTopComponent.jToolBar1.toolTipText")); // NOI18N
        jToolBar1.setMinimumSize(new java.awt.Dimension(24, 24));
        jToolBar1.setPreferredSize(new java.awt.Dimension(24, 24));
        add(jToolBar1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    @Override
    protected void componentActivated() {
        if (resumingState && url != null) {
            loadUrl();
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        p.setProperty("url", url.toString());
        p.setProperty("scroll", "" + getScrollPosition());

        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        String urlString = p.getProperty("url");
        try {
            this.url = new AlmuallimURL(urlString);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        String scrollValue = p.getProperty("scroll");
        if (scrollValue != null && !scrollValue.isEmpty()) {
            scroll = Integer.parseInt(scrollValue);
        }
        resumingState = true;
    }

    //init the javafx environment
    //webveiw to be precise
    private void initFx() {

        // create javafx panel for browser
        browserFxPanel = new JFXPanel();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(browserFxPanel, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        // create JavaFX scene
        JavaFX.invokeAndWait(new Runnable() {
            public void run() {
                createFxBrowser();
            }
        });

    }

    private int getScrollPosition() {
//        if (engine != null) {
//            Object o = engine.executeScript("$(window).scrollTop()");
//            int scroll = Integer.parseInt(o.toString());
//            return scroll;
//        }
        return 0;
    }

    private void setScrollPosition(int scroll) {
//        if (engine != null) {
//            engine.executeScript("$(window).scrollTop(" + scroll + ")");
//        }
    }

    private void createFxBrowser() {
        view = new WebView();
        view.setContextMenuEnabled(false);
        engine = view.getEngine();
//        enableFirebug(engine);
        engine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> t) {
                System.out.println(t.getData());
            }
        });
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State t, Worker.State t1) {
                if (t1 == Worker.State.SUCCEEDED) {
                    //ok the page is loaded...
                    setScrollPosition(scroll);
                    padeLoaded = true;
                    jsWindowObject = (JSObject) engine.executeScript("window");
                    installBrowserAddins();
                    firePropertyChange(Browser.PAGE_LOADED, null, "PAGE_LOADED");
                }
            }
        });
        browserFxPanel.setScene(new Scene(view));
    }

    /**
     * Enables Firebug Lite for debugging a webEngine. could not make it work..
     *
     * @param engine the webEngine for which debugging is to be enabled.
     */
    private static void enableFirebug(final WebEngine engine) {
        engine.executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
    }

    private void installBrowserAddins() {
        
        jToolBar1.removeAll();
        //register a callback for handling click events
        jsWindowObject.setMember("contextMenu", new ContextMenuActionListener());
        int menuItemCount = 0;
        int toolbarItemCount = 0;
        StringBuilder cmenuScript = new StringBuilder();
        cmenuScript.append("$('body').contextPopup({");
        cmenuScript.append("title: 'Al-muallim',");
        cmenuScript.append("items: [");

        for (BrowserAddIn browserAddIn : browserAddins) {
            String modules = browserAddIn.getSupportedModules();
            //if the mocule is not supported, ignore this addin
            boolean supported = modules.contains(url.getModuleName()) || "ALL".equals(modules);
            if (!supported) {
                continue;
            }
            //let the addin does some initialization
            browserAddIn.init(engine.getDocument(), getJSEngine(), view);
            //get the action
            Action a = browserAddIn.getAction();
            //add to context menu, if required
            if (browserAddIn.getDisplayPosition().contains(ActionDisplayPosition.CONTEXT_MENU)) {
                cmenuScript.append("{");
                cmenuScript.append(String.format("label: '%s'", a.getValue(Action.NAME)));
                cmenuScript.append(String.format("icon: '%s'", a.getValue(BrowserAddIn.IMAGE_URL)));
                cmenuScript.append(String.format("action: function() {contextMenu.actionPerformed('%s')}", a.getValue(Action.NAME)));
                cmenuScript.append("},");
                menuItemCount++;
            }
            //or to toolbar
            if (browserAddIn.getDisplayPosition().contains(ActionDisplayPosition.TOOLBAR)) {
                if (browserAddIn.separatorBefore()) {
                    jToolBar1.addSeparator();
                }
                jToolBar1.add(a);
                if (browserAddIn.separatorAfter()) {
                    jToolBar1.addSeparator();
                }
                toolbarItemCount++;
            }

        }
        cmenuScript.append("]");
        cmenuScript.append("});");
        if (menuItemCount != 0) {
            engine.executeScript(cmenuScript.toString());
        }
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
    }

    private class ContextMenuActionListener {

        public void actionPerformed(String key) {
            for (BrowserAddIn browserAddIn : browserAddins) {
                Action a = browserAddIn.getAction();
                String name = a.getValue(Action.NAME).toString();
                if (name.equals(key)) {
                    a.actionPerformed(new ActionEvent(null, 1, "", 1));
//                    browserAddIn.onItemClicked(browserAddIn.getMenuItem(), engine.getDocument(), url);
                }
            }
        }
    }
}
