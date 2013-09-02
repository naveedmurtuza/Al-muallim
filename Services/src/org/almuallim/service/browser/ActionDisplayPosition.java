package org.almuallim.service.browser;

import java.util.EnumSet;

/**
 *
 * @author Naveed Quadri
 */
public enum ActionDisplayPosition {

    CONTEXT_MENU,
    NONE,
    TOOLBAR;
    public static final EnumSet<ActionDisplayPosition> BOTH = EnumSet.of(TOOLBAR, CONTEXT_MENU);
}