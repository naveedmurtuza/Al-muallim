package org.almuallim.browser.addins;

import com.google.common.io.Files;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.EnumSet;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import org.almuallim.service.browser.ActionDisplayPosition;
import org.almuallim.service.browser.ActionDisplayStyle;
import org.almuallim.service.browser.BrowserAddIn;
import org.almuallim.service.browser.JSEngine;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = BrowserAddIn.class, position = 11111)
public class SaveCurrentDomAddin implements BrowserAddIn {

    private Document dom;
    private JSEngine engine;
    private WebView view;
    private Action action;
    private final ImageIcon icon;

    public SaveCurrentDomAddin() {
        icon = new ImageIcon(getClass().getResource("reload.png"));
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
    public void init(Document dom, JSEngine engine, WebView view) {
        this.dom = dom;
        this.engine = engine;
        this.view = view;

    }

    @Override
    public Action getAction() {

        return new ReloadAction();
    }

    @Override
    public boolean separatorAfter() {
        return true;
    }

    @Override
    public boolean separatorBefore() {
        return false;
    }

    @Override
    public String getSupportedModules() {
        return "ALL";
    }

    private class ReloadAction extends AbstractAction {

        public ReloadAction() {
            super("Save Dom", icon);
            putValue(Action.SHORT_DESCRIPTION, "Reload (Ctrl F5)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F5"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.showSaveDialog(null);
            final File f = fileChooser.getSelectedFile();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    String html = view.getEngine().executeScript("$('html').html();").toString();
                    try {
                        Files.write(html, f, Charset.forName("UTF-8"));
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
        }
    }
}
