/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jidesoft;

import com.jidesoft.plaf.LookAndFeelFactory;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        LookAndFeelFactory.installJideExtension(LookAndFeelFactory.EXTENSION_STYLE_ECLIPSE3X_WITHOUT_MENU);
        
    }
}
