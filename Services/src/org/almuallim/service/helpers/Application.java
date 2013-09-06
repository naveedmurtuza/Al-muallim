package org.almuallim.service.helpers;

import org.openide.modules.Places;

/**
 *
 * @author Naveed
 */
public class Application {

    public static String getHome() {
        return Places.getUserDirectory().getAbsolutePath();
    }
}
