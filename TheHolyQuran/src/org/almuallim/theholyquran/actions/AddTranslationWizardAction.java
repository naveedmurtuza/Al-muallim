/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.almuallim.service.helpers.LanguageUtils;
import org.almuallim.theholyquran.ModuleActivation;
import org.almuallim.theholyquran.api.TheHolyQuran;
import org.almuallim.theholyquran.parsers.TanzilParser;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbPreferences;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
@ActionID(category = "...", id = "org.almuallim.theholyquran.AddTranslationWizardAction")
@ActionRegistration(displayName = "Add Translation Wizard")
@ActionReference(path = "Menu/Tools/The Holy Quran", position = 1111)
public final class AddTranslationWizardAction implements ActionListener, Runnable {

    private static final Logger LOG = Logger.getLogger(AddTranslationWizardAction.class.getName());
    private WizardDescriptor wiz;

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean activated = NbPreferences.forModule(getClass()).getBoolean("activated", false);
        if (!activated) {
            NotifyDescriptor nd = new NotifyDescriptor.Confirmation("The Holy Quran Module is not activated. Do you want to activate it now", "Module not activated",NotifyDescriptor.YES_NO_OPTION);
            Object res = DialogDisplayer.getDefault().notify(nd);
            if (res == NotifyDescriptor.YES_OPTION) {
                boolean cancelled = ModuleActivation.activate();
                if (cancelled) {
                    return;
                }
            } else {
                return;
            }
        }
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(new AddTranslationWizardPanel1());
        panels.add(new AddTranslationWizardPanel2());
        panels.add(new AddTranslationWizardPanel3());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Add Translation Wizard");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            // do something
            Thread th = new Thread(this);
            th.start();
        }
    }

    @Override
    public void run() {
        ProgressHandle p = ProgressHandleFactory.createHandle("Adding Translation ...");

        p.start();

        File file = (File) wiz.getProperty("PROP_FILE");
        TanzilParser parser = (TanzilParser) wiz.getProperty("PROP_PARSER");
        String language = wiz.getProperty("PROP_LANGUAGE").toString();
        boolean rtl = (boolean) wiz.getProperty("PROP_RTL");
        String translator = wiz.getProperty("PROP_TRANSLATOR_NAME").toString();
        String localizedName = wiz.getProperty("PROP_TRANSLATOR_LOCALIZED_NAME").toString();
        String bio = wiz.getProperty("PROP_TRANSLATOR_BIO").toString();
        LOG.log(Level.INFO, "Adding translation => '{' translator ={0} , language = {1} ({2} )'}'", new Object[]{translator, language, rtl ? "RTL" : "LTR"});
        long duration;
        try {
            long start = System.currentTimeMillis();
            TheHolyQuran.getInstance().addTranslation(file.getAbsolutePath(), parser, LanguageUtils.getLanguageCode(language), rtl, translator, localizedName, bio);
            long end = System.currentTimeMillis();
            LOG.log(Level.INFO, "time taken to add translation: {0} seconds", ((end - start) / 1000));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            p.finish();
        }

    }
}
