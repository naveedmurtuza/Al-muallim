/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.browseraddin.fontstyle;

import com.google.common.io.Files;
import com.osbcp.cssparser.CSSParser;
import com.osbcp.cssparser.Rule;
import com.osbcp.cssparser.Rules;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
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
import org.almuallim.service.helpers.Application;
import org.almuallim.theholyquran.ModuleConstants;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = BrowserAddIn.class, position = 1000)
public class FontStyleAddin implements BrowserAddIn {

    private static final String CSS_FILE_PATH = Application.getHome() + File.separatorChar + ModuleConstants.MODULE_NAME + File.separator + "FontStyleAddin" + File.separator + "fontStyles.css";
    private final ImageIcon icon;
    private Document dom;
    private JSEngine engine;
    private WebView view;
    private Action action;
    private List<Rule> rules = new ArrayList<>();

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
                    //load the css file, if exists
                    File cssFile = new File(CSS_FILE_PATH);
                    cssFile.getParentFile().mkdirs();
                    if (cssFile.exists()) {
                        rules = CSSParser.parse(Files.toString(cssFile, Charset.defaultCharset()));
                        final String script = MessageFormat.format("insertStylesheet(''{0}'');", Utilities.toURI(cssFile));
                        
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                FontStyleAddin.this.engine.executeScript(script);
                            }
                        });
                    }
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }).start();
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
            final FontStylePanel panel = new FontStylePanel(rules);
            DialogDescriptor dd = new DialogDescriptor(panel, "Select Translations",true,new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(e.getActionCommand().equalsIgnoreCase("OK"))
                    {
                      List<Rule> rules =  panel.getRules();
                        try {
                            Rules.save(rules,CSS_FILE_PATH);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });

            DialogDisplayer.getDefault().notify(dd);
            //panel.save();

        }
    }
}
