package org.almuallim.service.browser;

import java.util.EnumSet;
import javafx.scene.web.WebView;
import javax.swing.Action;
import org.w3c.dom.Document;

/**
 * 
 * @author Naveed Quadri
 */
public interface BrowserAddIn {

    public final String IMAGE_URL = "imageUrl";
    
    /**
     * for contextmenu actions, the action should also contain a clientproperty for <code>IMAGE_URL</code>
     * @return 
     */
    public Action getAction();

    /**
     * The display style for the action. 
     * @return an enumset containign one or more values of <code>ActionDisplayStyle</code>
     */
    public EnumSet<ActionDisplayStyle> getDisplayStyle();
    
    /**
     * The display position for the action. To be displayed in Toolbar or context menu or both
     * @return an enumset containign one or more values of <code>ActionDisplayPosition</code>
     */
    public EnumSet<ActionDisplayPosition> getDisplayPosition();
    
    /**
     * 
     * @return the position of the action
     * @deprecated use position attribute in service annotation instead
     */
    public int getPosition();
    
    /**
     * Do all initialization of the plugin here. 
     * @param dom The Document
     * @param engine JSEngine
     * @param view WebView
     */
    public void init(Document dom, JSEngine engine, WebView view);

    /**
     * whether to display a separator after this action
     * @return true to display a separator after, false otherwise
     */
    public boolean separatorAfter();
    
    /**
     * whether to display a separator before this action
     * @return true to display a separator before, false otherwise
     */
    public boolean separatorBefore();
    
    /**
     * should return 'ALL' if supports all modules. (eg. RefreshAddin)
     * otherwise a comma separated list of all the supported modules. Should be the same as the module name
     * in <code>AlmuallimURL</code>
     * @return ALL to support all modules, otherwise a comma separated list of modules
     */
    public String getSupportedModules();
    
}
