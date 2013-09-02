package org.almuallim.service.url;

import java.io.File;
import java.util.Collection;
import org.openide.util.Lookup;

/**
 * To be able to open a URL, module should implement this interface
 *
 * @author Naveed Quadri
 */
public interface AlmuallimURLOpener {

    /**
     * For a given url, generates an html and saves it in file.
     *
     * @param url
     * @param file
     */
    public void generateHtml(AlmuallimURL url, File file);

    /**
     * convenience class to resolve a
     * <code>AlmuallimURL</code> to its
     * <code>AlmuallimURLOpener</code>
     */
    public static class Resolver {

        /**
         * Looks up all the implementations of
         * <code>AlmuallimURLOpener</code> and finds a matching opener for this
         * class and finds
         *
         * @param className the className parameter in the url. Use the
         * AlmuallimURL.ClassNameKey constant to retrieve the classpath
         * @return
         */
        public static AlmuallimURLOpener resolve(String className) {
            AlmuallimURLOpener urlOpener = null;
            Collection<? extends AlmuallimURLOpener> urlOpeners = Lookup.getDefault().lookupAll(AlmuallimURLOpener.class);
            for (AlmuallimURLOpener almuallimURLOpener : urlOpeners) {
                if (almuallimURLOpener.getClass().getCanonicalName().equals(className)) {
                    urlOpener = almuallimURLOpener;
                    break;
                }
            }
            return urlOpener;
        }
    }
}
