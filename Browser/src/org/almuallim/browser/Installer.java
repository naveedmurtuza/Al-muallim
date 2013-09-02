/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.browser;

import java.util.HashSet;
import java.util.Set;
import org.almuallim.service.url.AlmuallimURL;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall implements LookupListener {

    private static Lookup.Result<AlmuallimURL> almuallimUrls;
    public static Set uniqueTCs = new HashSet();

    @Override
    public void restored() {
        almuallimUrls = Utilities.actionsGlobalContext().lookupResult(AlmuallimURL.class);
        almuallimUrls.addLookupListener(this);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (almuallimUrls.allInstances().iterator().hasNext()) {
            AlmuallimURL c = almuallimUrls.allInstances().iterator().next();
            if (uniqueTCs.add(c)) {
                BrowserTopComponent cetc = new BrowserTopComponent();
                cetc.setUrl(c);
                cetc.open();
                cetc.requestActive();
            } else {
                //In this case, the TopComponent is already open, but needs to become active:
                for (TopComponent tc : WindowManager.getDefault().findMode("editor").getTopComponents()) {
                    BrowserTopComponent btc = (BrowserTopComponent) tc;
                    if (btc.getUrl().equals(c)) {
                        btc.requestActive();
                        break;
                    }
                }
            }
        }
    }
}
