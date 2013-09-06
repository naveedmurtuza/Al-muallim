/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.browseraddin.translation;

import org.almuallim.theholyquran.TranslatorsPanel;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import netscape.javascript.JSObject;
import org.almuallim.service.browser.BrowserAddIn;
import org.almuallim.service.browser.JSEngine;
import org.almuallim.service.browser.ActionDisplayPosition;
import org.almuallim.service.browser.ActionDisplayStyle;
import org.almuallim.service.helpers.EscapeUtils;
import org.almuallim.service.helpers.JavaFX;
import org.almuallim.service.url.AlmuallimURL;
import org.almuallim.theholyquran.ModuleConstants;
import org.almuallim.theholyquran.api.TheHolyQuran;
import org.almuallim.theholyquran.api.VerseCollection;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;

/**
 *
 * @author Naveed
 */
@ServiceProvider(service = BrowserAddIn.class, position = 10)
public class TranslationAddin implements BrowserAddIn {

    private final Set<Integer> translatorIds;
    private WebView view;
    private JSEngine engine;
    private Document dom;
    private ImageIcon icon;
    private Action action;
    private int chapter;
    private TheHolyQuran theHolyQuran = TheHolyQuran.getInstance();
    private String url;

    public TranslationAddin() {
        icon = new ImageIcon(getClass().getResource("translate.png"));
        Preferences preferences = NbPreferences.forModule(getClass());
        String ids = preferences.get("SELECTED_TRANSLATOR_IDS", "");
        translatorIds = new HashSet<>();
        for (String id : ids.split(",")) {
            if (!id.isEmpty()) {
                translatorIds.add(Integer.parseInt(id));
            }
        }
    }

    @Override
    public void init(Document dom, JSEngine engine, WebView view) {
        this.dom = dom;
        this.engine = engine;
        this.view = view;
        new Thread(new Runnable() {
            @Override
            public void run() {
                JavaFX.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        url = TranslationAddin.this.engine.executeScript("getUrl();").toString();
                    }
                });
                String chapterVal = AlmuallimURL.getParameter(url, "chapter");
                String transVal = AlmuallimURL.getParameter(url, "translator");
                final String verseVal = AlmuallimURL.getParameter(url, "verse");
                if (!Strings.isNullOrEmpty(transVal)) {
                    final int transId = Integer.parseInt(transVal);
                    if (!translatorIds.contains(transId)) {
                        translatorIds.add(transId);
                    }

                }
                chapter = Integer.parseInt(chapterVal);
                for (Integer translatorId : translatorIds) {
                    VerseCollection verseCollection = theHolyQuran.translateVerses(chapter, translatorId);
                    if (verseCollection == null) {
                        //possibly the translatotion has been deleted
                        translatorIds.remove(translatorId);
                    } else {
                        addTranslation(verseCollection.toJSON());
                    }
                }
                if (!Strings.isNullOrEmpty(verseVal)) {
                    JavaFX.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            String selector = "article[data-verse-index=" + verseVal + "]";
                            TranslationAddin.this.engine.executeScript(String.format("$('%s').ensureVisible()", selector));
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public Action getAction() {
        if (action == null) {
            action = new TranslationAction();
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

//    @Override
//    public int getPosition() {
//        return 10;
//    }
    @Override
    public boolean separatorAfter() {
        return false;
    }

    @Override
    public boolean separatorBefore() {
        return true;
    }

    private void removeTranslation(final int translatorId) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                engine.executeScript(String.format("", translatorId));
            }
        });

    }

    private void addTranslation(final String json) {
        System.out.println(EscapeUtils.escapeJava(json));
        executeJScript("addTranslation(\"" + EscapeUtils.escapeJava(json) + "\");");


    }

    private void executeJScript(final String script) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                engine.executeScript(script);
            }
        });
    }

    @Override
    public String getSupportedModules() {
        return ModuleConstants.MODULE_NAME;
    }

    private class TranslationAction extends AbstractAction {

        public TranslationAction() {
            super("Translations", icon);

            putValue(Action.SHORT_DESCRIPTION, "Translations (Ctrl + T)");
//            putValue(Action.NAME, "Translations");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control T"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Object executeScript = engine.executeScript("getSelectItem();");
                    Object member = ((JSObject)executeScript).getMember("ref");
                }
            });
            TranslatorsPanel panel = new TranslatorsPanel();
            panel.buildTree(translatorIds);
            DialogDescriptor dd = new DialogDescriptor(panel, "Select Translations");

            Object res = DialogDisplayer.getDefault().notify(dd);
            if (res != NotifyDescriptor.OK_OPTION) {
                return;
            }
            final Set<Integer> selectedIds = new HashSet<>();
            Collections.addAll(selectedIds, panel.getSelectedIds());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    executeJScript("showLoader();");
                    Sets.SetView<Integer> difference = Sets.difference(translatorIds, selectedIds);
                    for (Integer integer : difference) {
                        removeTranslation(integer);
                    }
                    translatorIds.removeAll(difference);
                    Sets.SetView<Integer> idsToAdd = Sets.difference(selectedIds, translatorIds);
                    for (Integer integer : idsToAdd) {
                        VerseCollection verseCollection = theHolyQuran.translateVerses(chapter, integer);
                        addTranslation(verseCollection.toJSON());
                    }
                    translatorIds.addAll(idsToAdd);
                    String toSave = Arrays.toString(translatorIds.toArray()).replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
                    NbPreferences.forModule(getClass()).put("SELECTED_TRANSLATOR_IDS", toSave);
                    executeJScript("hideLoader();");
                }
            }).start();



        }
    }
}
