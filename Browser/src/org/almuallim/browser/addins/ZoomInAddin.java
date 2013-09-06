/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.browser.addins;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EnumSet;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
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
@ServiceProvider(service = BrowserAddIn.class, position = 2)
public class ZoomInAddin implements BrowserAddIn {

    private WebView view;
    private JSEngine engine;
    private Document dom;
    private ImageIcon icon;
    private Action action;

    public ZoomInAddin() {
        icon = new ImageIcon(getClass().getResource("zoom_in.png"));
    }

    @Override
    public void init(Document dom, JSEngine engine, final WebView view) {
        this.dom = dom;
        this.engine = engine;
        this.view = view;
        this.view.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent t) {
                if (t.isControlDown()) {
                    switch (t.getTextDeltaYUnits()) {
                        case NONE:
                            double pixels = t.getDeltaY();
                            double zoom = view.getZoom();
                            if (pixels < 0) {
                                zoom *= 0.75;
                            } else {
                                zoom /= 0.75;
                            }
                            view.setZoom(zoom);
                            // scroll about event.getDeltaY() pixels
                            break;
                    }
                }
            }
        });
    }

    @Override
    public Action getAction() {
        
        return new ZoomInAction();
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
return "ALL";    }

    private class ZoomInAction extends AbstractAction {

        public ZoomInAction() {
            super("Zoom In", icon);
            putValue(Action.SHORT_DESCRIPTION, "Zoom In (Ctrl + +)");
//            putValue(Action.NAME, "Zoom In");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control +"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    view.setZoom(view.getZoom() / 0.75);
                }
            });
        }
    }
}
