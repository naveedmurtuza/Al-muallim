/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.browser.addins;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EnumSet;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.almuallim.service.browser.BrowserAddIn;
import org.almuallim.service.browser.JSEngine;
import org.almuallim.service.browser.ActionDisplayPosition;
import org.almuallim.service.browser.ActionDisplayStyle;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;

/**
 *
 * @author Naveed
 */
@ServiceProvider(service = BrowserAddIn.class, position = 4)
public class ZoomOutAddin implements BrowserAddIn {

    private WebView view;
    private JSEngine engine;
    private Document dom;
    private ImageIcon icon;
    private Action action;

    public ZoomOutAddin() {
        icon = new ImageIcon(getClass().getResource("zoom_out.png"));
    }

    @Override
    public void init(Document dom, JSEngine engine, WebView view) {
        this.dom = dom;
        this.engine = engine;
        this.view = view;

    }

    @Override
    public Action getAction() {

        return new ZoomOutAction();
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
        return false;
    }

    @Override
    public String getSupportedModules() {
        return "ALL";
    }

    private class ZoomOutAction extends AbstractAction {

        public ZoomOutAction() {
            super("Zoom Out", icon);
            putValue(Action.SHORT_DESCRIPTION, "Zoom Out (Ctrl + -)");
//            putValue(Action.NAME, "Zoom Out");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control -"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    view.setZoom(view.getZoom() * 0.75);
                }
            });
        }
    }
}
