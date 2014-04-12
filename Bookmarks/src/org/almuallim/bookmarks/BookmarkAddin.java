package org.almuallim.bookmarks;

import java.awt.event.ActionEvent;
import java.util.EnumSet;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import netscape.javascript.JSObject;
import org.almuallim.service.browser.ActionDisplayPosition;
import org.almuallim.service.browser.ActionDisplayStyle;
import org.almuallim.service.browser.BrowserAddIn;
import org.almuallim.service.browser.JSEngine;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = BrowserAddIn.class, position = 25)
public class BookmarkAddin implements BrowserAddIn {

    private WebView view;
    private JSEngine engine;
    private Document dom;
    private final ImageIcon icon;

    public BookmarkAddin() {
         icon = new ImageIcon(getClass().getResource("bookmark.png"));
    }

    
    @Override
    public Action getAction() {
       return new BookmarkAction();
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

    private class BookmarkAction extends AbstractAction {

        public BookmarkAction() {
            super("Toggle Bookmark", icon);
            putValue(Action.SHORT_DESCRIPTION, "Toggle Bookmark (Ctrl + D)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control d"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    JSObject o =(JSObject) engine.executeScript("getSelectItem();");
                    int debug = 9;
                }
            });
        }
    }

}
