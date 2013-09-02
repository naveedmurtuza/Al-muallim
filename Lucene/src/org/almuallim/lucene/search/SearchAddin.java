package org.almuallim.lucene.search;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.EnumSet;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.almuallim.service.browser.Browser;
import org.almuallim.service.browser.BrowserAddIn;
import org.almuallim.service.browser.JSEngine;
import org.almuallim.service.browser.ActionDisplayPosition;
import org.almuallim.service.browser.ActionDisplayStyle;
import org.almuallim.service.helpers.JavaFX;
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
@ServiceProvider(service = BrowserAddIn.class)
public class SearchAddin implements BrowserAddIn {

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
    public int getPosition() {
        return 1000;
    }

    @Override
    public void init(Document dom, JSEngine engine, WebView view) {
        this.dom = dom;
        this.engine = engine;
        this.view = view;

        //register java function
        engine.registerJavaFunction("search", new SearchButtonAction());
        engine.registerJavaFunction("open", new TestOpen());

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

    public class TestOpen {

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
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Object o = engine.executeScript("$('html').clone().html();");

//                           ((JSObject)o).eval(NAME);
                    int f = 1;

                }
            });
            System.out.println(query);
            final Gson gson = new Gson();
            BasicSearchProvider searchProvider = new BasicSearchProvider();
            try {
                searchProvider.search(query, new SearchCallback() {
                    @Override
                    public void resultFound(String term, float score, SearchResult result) {
                        //                            jsEngine.executeScript("add");

                        final String json = gson.toJson(result);
                        System.out.println(json);
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
                        //("#search").removeClass("hidden")
                        engine.executeScript("$('#search').removeClass('main').addClass('header');");

                        //install waypoint to fetch more results when the 
                        //user scrolls down
                    }
                });
            } catch (Exception ex) {
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
