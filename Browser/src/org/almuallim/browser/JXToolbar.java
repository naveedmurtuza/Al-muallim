/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.browser;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 *
 * @author Naveed Quadri
 * http://stackoverflow.com/questions/8517435/having-trouble-to-find-shortcut-key-to-jtoolbar-in-swings
 */
public class JXToolbar extends JToolBar {

    @Override
    public JButton add(Action a) {
        JButton button = super.add(a);
        KeyStroke stroke = (KeyStroke) a.getValue(Action.ACCELERATOR_KEY);
        if (stroke != null) {
            button.getActionMap().put(a.getValue(Action.NAME), a);
            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    (KeyStroke) a.getValue(Action.ACCELERATOR_KEY), a.getValue(Action.NAME));
        }
        return button;
    }
}
