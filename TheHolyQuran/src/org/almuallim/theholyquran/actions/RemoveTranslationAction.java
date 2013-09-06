package org.almuallim.theholyquran.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.almuallim.service.helpers.LanguageUtils;
import org.almuallim.theholyquran.TranslatorsPanel;
import org.almuallim.theholyquran.api.TheHolyQuran;
import org.almuallim.theholyquran.api.Translator;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "org.almuallim.theholyquran.actions.RemoveTranslationAction")
@ActionRegistration(
        displayName = "#CTL_RemoveTranslationAction")
@ActionReference(path = "Menu/Tools/The Holy Quran", position = 1211)
@Messages("CTL_RemoveTranslationAction=Remove Translation")
public final class RemoveTranslationAction implements ActionListener, Runnable {

    private static final Logger LOG = Logger.getLogger(RemoveTranslationAction.class.getName());
    private Translator[] todelete;

    @Override
    public void actionPerformed(ActionEvent e) {
        TranslatorsPanel panel = new TranslatorsPanel();
        panel.buildTree();
        DialogDescriptor dd = new DialogDescriptor(panel, "Select Translations");

        Object res = DialogDisplayer.getDefault().notify(dd);
        if (res != NotifyDescriptor.OK_OPTION) {
            return;
        }
        todelete = panel.getSelectedTranslators();
        StringBuilder sb = new StringBuilder("Are you sure to delete these translation(s)");
        sb.append(System.lineSeparator());
        for (Translator translator : todelete) {
            sb.append(translator.getName())
                    .append(" [ ")
                    .append(LanguageUtils.getLanguageName(translator.getLanguage().getIso2Code()))
                    .append(" ]").append(System.lineSeparator());
        }
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(sb.toString(), "Delete", NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.WARNING_MESSAGE);
        res = DialogDisplayer.getDefault().notify(nd);
        if (res != NotifyDescriptor.OK_OPTION) {
            return;
        }
        // do something
        Thread th = new Thread(this);
        th.start();

    }

    @Override
    public void run() {
        ProgressHandle p = ProgressHandleFactory.createHandle("Removing Translations ...");
        p.start();
        try {
            long start = System.currentTimeMillis();
            if (todelete != null) {
                for (Translator translator : todelete) {
                    TheHolyQuran.getInstance().deleteTranslation(translator);
                }
            }
            long end = System.currentTimeMillis();
            LOG.log(Level.INFO, "time taken to add translation: {0} seconds", ((end - start) / 1000));
        } finally {
            p.finish();
        }
    }
}
