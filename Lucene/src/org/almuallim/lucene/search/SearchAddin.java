package org.almuallim.lucene.search;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.almuallim.service.browser.Browser;
import org.almuallim.service.browser.BrowserAddIn;
import org.almuallim.service.browser.JSEngine;
import org.almuallim.service.browser.ActionDisplayPosition;
import org.almuallim.service.browser.ActionDisplayStyle;
import org.almuallim.service.search.SearchCallback;
import org.almuallim.service.search.SearchResult;
import org.almuallim.service.url.AlmuallimURL;
import org.apache.lucene.index.CorruptIndexException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = BrowserAddIn.class, position = 9999)
public class SearchAddin implements BrowserAddIn {
        private static final Logger LOG = Logger.getLogger(SearchAddin.class.getName());

    private WebView view;
    private JSEngine engine;
    private Document dom;

    @Override
    public Action getAction() {
        return null;
    }

    @Override
    public EnumSet<ActionDisplayStyle> getDisplayStyle() {
        return EnumSet.of(ActionDisplayStyle.NONE);
    }

    @Override
    public EnumSet<ActionDisplayPosition> getDisplayPosition() {
        return EnumSet.of(ActionDisplayPosition.NONE);
    }

    

    @Override
    public void init(Document dom, JSEngine engine, WebView view) {
        this.dom = dom;
        this.engine = engine;
        this.view = view;

        //register java function
        engine.registerJavaFunction("search", new SearchButtonAction());
        engine.registerJavaFunction("open", new OpenResult());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                SearchAddin.this.engine.executeScript("doInit();");
            }
        });
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
        return ModuleConstants.MODULE_NAME;
    }

    public class OpenResult {

        private Browser browser;

        public void openResult(final String url) {
            if (browser == null) {
                browser = Lookup.getDefault().lookup(Browser.class);
            }
            try {
                final AlmuallimURL urlObj = new AlmuallimURL(url);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        browser.navigate(urlObj);
                    }
                });
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public class SearchButtonAction {

        public void actionPerformed(final String query, int pageNumber) {
            
            System.out.println(query);
            final Gson gson = new Gson();
            BasicSearchProvider searchProvider = new BasicSearchProvider();
            try {
                searchProvider.search(query, new SearchCallback() {
                    @Override
                    public void resultFound(String term, float score, SearchResult result) {

                        final String json = gson.toJson(result);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                engine.executeScript(String.format("addSearchResult('%s');", json));
                            }
                        });
                    }
                }, pageNumber);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        engine.executeScript("removeAnimation();");
                        engine.executeScript("$('#search').removeClass('main').addClass('header');");
                    }
                });
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error searching index", ex);
                final StringBuilder message = new StringBuilder();
                String type;
                if (ex instanceof CorruptIndexException) {
                    message.append("The index has been corrupted. please rebuild the index");
                } else if (ex instanceof IOException) {
                    message.append("No index found to start searching. Please start adding documents to the application to build search index."
                            + "To rebuild the index ... <TODO>");
                } else {
                    message.append("Unknown error! [").append(ex.getMessage()).append("]");
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        engine.executeScript(String.format("notifyError('%s');", message.toString()));
                    }
                });
            }
        }

        public void searchResultClicked() {
        }
    }
}
