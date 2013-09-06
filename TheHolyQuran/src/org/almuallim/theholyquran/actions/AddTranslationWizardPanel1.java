/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.almuallim.theholyquran.api.TheHolyQuran;
import org.almuallim.theholyquran.api.VerseCollection;
import org.almuallim.theholyquran.parsers.TanzilParser;
import org.almuallim.theholyquran.parsers.TanzilTextParser;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class AddTranslationWizardPanel1 implements WizardDescriptor.AsynchronousValidatingPanel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private AddTranslationVisualPanel1 component;
    private WizardDescriptor wiz;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.

    @Override
    public AddTranslationVisualPanel1 getComponent() {
        if (component == null) {
            component = new AddTranslationVisualPanel1();
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
        return isValid;
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
        // use wiz.getProperty to retrieve previous panel state
        this.wiz = wiz;
        getComponent().addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        wiz.putProperty("PROP_FILE", getComponent().getSelectedFile());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        switch (propName) {
            case "PROP_FILE_SELECTED":
                boolean old = isValid;
                if (evt.getNewValue() != null) {
                    isValid = true;
                } else {
                    isValid = false;
                }
                fireChangeEvent(this, old, isValid);
                break;
        }
    }

    protected final void fireChangeEvent(
            Object source, boolean oldState, boolean newState) {
        if (oldState != newState) {
            ChangeEvent ev = new ChangeEvent(source);
            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(ev);
            }
        }
    }

    private void setMessage(String message) {
        wiz.getNotificationLineSupport().setErrorMessage(message);
    }

    /**
     * Called synchronously from UI thread when Next of Finish buttons clicked.
     * It allows to lock user input to assure official data for background
     * validation.
     */
    @Override
    public void prepareValidation() {
    }

    /**
     * Is called in separate thread when Next of Finish buttons are clicked and
     * allows deeper check to find out that panel is in valid state and it is ok
     * to leave it.
     *
     * @throws WizardValidationException when validation fails
     */
    @Override
    public void validate() throws WizardValidationException {
        try {
            String path = getComponent().getSelectedFile().getAbsolutePath();
            TanzilParser parser = TheHolyQuran.getInstance().detectParser(path);
            VerseCollection collection = TheHolyQuran.getInstance().previewTranslation(path, parser);
            wiz.putProperty("PROP_VERSE_COLLECTION", collection);
            wiz.putProperty("PROP_PARSER", parser);
//            setMessage("");

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
//            setMessage("Error parsing translation!" + System.lineSeparator() + ex.getMessage());
            isValid = false;
        }
    }
}
