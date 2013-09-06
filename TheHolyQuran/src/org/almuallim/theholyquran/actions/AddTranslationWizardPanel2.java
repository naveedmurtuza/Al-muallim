/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.almuallim.theholyquran.api.VerseCollection;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class AddTranslationWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private AddTranslationVisualPanel2 component;
    private WizardDescriptor wiz;
    private VerseCollection collection;
    private boolean isValid;
    private final EventListenerList listeners = new EventListenerList();

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public AddTranslationVisualPanel2 getComponent() {
        if (component == null) {
            component = new AddTranslationVisualPanel2();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        collection = (VerseCollection) wiz.getProperty("PROP_VERSE_COLLECTION");
        getComponent().updatePreview(collection);
        getComponent().addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty("PROP_LANGUAGE", getComponent().getLanguage());
        wiz.putProperty("PROP_RTL", getComponent().isRtl());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        boolean oldState = isValid;
        switch (propName) {
            case "PROP_RTL_CHANGE":
                getComponent().updatePreview(collection);
                break;
            case "PROP_LANG_SELECTED":
                checkValidity();
                fireChangeEvent(this, oldState, isValid);
                break;

        }
    }

    protected final void fireChangeEvent(Object source, boolean oldState, boolean newState) {
        if (oldState != newState) {
            ChangeEvent ev = new ChangeEvent(source);
            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(ev);
            }
        }
    }

    private String checkValidity() {
        isValid = getComponent().languageSelected();
        return "";
    }
}
