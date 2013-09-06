/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import static javax.swing.SwingWorker.StateValue.DONE;
import org.almuallim.service.database.Database;
import org.almuallim.service.helpers.Application;
import org.almuallim.theholyquran.api.TheHolyQuran;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

/**
 *
 * @author Naveed
 */
public class ModuleActivation extends SwingWorker<Boolean, String> {

    private static final Logger LOG = Logger.getLogger(ModuleActivation.class.getName());

    @Override
    protected Boolean doInBackground() throws Exception {
        LOG.fine("Looking up database imlpementations");
        Database database = Lookup.getDefault().lookup(Database.class);
        try (Connection connection = database.getConnection()) {
//            drop all tables
            LOG.info("Dropping all existing tables (for TheHolyQuran Module) if any");
            try (Statement stmt = connection.createStatement()) {
                stmt.addBatch("DROP TABLE IF EXISTS CHAPTER");
                stmt.addBatch("DROP TABLE IF EXISTS LANGUAGE");
                stmt.addBatch("DROP TABLE IF EXISTS CHAPTERNAMES");
                stmt.addBatch("DROP TABLE IF EXISTS TRANSLATOR");
                stmt.addBatch("DROP TABLE IF EXISTS VERSE");
                stmt.addBatch("DROP TABLE IF EXISTS VERSE_I18N");
                stmt.executeBatch();
            }
            try (Statement stmt = connection.createStatement()) {
                String path;
                try (ResultSet rs = stmt.executeQuery("CALL DATABASE_PATH()")) {
                    rs.next();
                    path = rs.getString(1);
                    int index = path.lastIndexOf(':');
                    if (index > 1) {
                        path = path.substring(index + 1);
                    }
                }
                //check lucene index
//                LOG.info("Deleting lucene index");
//                FileUtils.deleteDirectory(new File(path));
            }
            LOG.info("Creating tables (TheHolyQuran module)");
            TheHolyQuran.onCreateDatabase(connection);
        }
        TextToPngImage tti = new TextToPngImage();
        long start = System.currentTimeMillis();
        try {
            LOG.info("Converting verses to PNGs");
            String dir = Application.getHome() + File.separatorChar + "TheHolyQuran" + File.separatorChar + "svg" + File.separatorChar;
            new File(dir).mkdirs();
            System.out.println(Toolkit.getDefaultToolkit().getScreenSize());
            Scanner scanner = new Scanner(TextToPngImage.class.getClassLoader().getResourceAsStream("org/almuallim/theholyquran/data/quran-noor-e-hidayat"), "utf-8");
            int index = 1;
            setProgress(0);
            while (scanner.hasNextLine()) {
                if (isCancelled()) {
                    break;
                }
                String text = scanner.nextLine();
                if (text.isEmpty() || text.startsWith("#")) {
                    continue;
                }
                tti.convertToImage(text, 890, 10, 10, Color.black, dir + index + ".png");
                float progress = (index / 6236f) * 100f;
                setProgress((int) progress);
                index++;
            }
        } catch (IOException ex) {
            Logger.getLogger(ModuleActivation.class.getName()).log(Level.SEVERE, null, ex);
        }
        long end = System.currentTimeMillis();
        long timeTakenMs = end - start;
        System.out.println((timeTakenMs / 1000) + " seconds");
        LOG.info("TheHolyQuran module initialized");
        return true;

    }

    @Override
    protected void process(List<String> chunks) {
        super.process(chunks); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void done() {
        super.done(); 
    }

    public static boolean activate() {
        LOG.log(Level.INFO, "Activating The Holy Quran Module..");
        final ModuleActivation activate = new ModuleActivation();
        final ActivateModulePanel panel = new ActivateModulePanel();
        DialogDescriptor d = new DialogDescriptor(panel, "String", true, new Object[]{"Cancel"}, "Cancel", DialogDescriptor.DEFAULT_ALIGN, HelpCtx.DEFAULT_HELP, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activate.cancel(true);
            }
        });
        final Dialog dialog = DialogDisplayer.getDefault().createDialog(d);
        activate.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();
                switch (property) {
                    case "progress":
                        panel.setProgress((Integer) evt.getNewValue());
                        break;
                    case "state":
                        switch ((SwingWorker.StateValue) evt.getNewValue()) {
                            case DONE:
                                dialog.setVisible(false);
                                break;
                        }
                }
            }
        });
        activate.execute();
        dialog.setVisible(true);
        boolean cancelled = activate.isCancelled();
        if (!cancelled) {
            NbPreferences.forModule(QuranExplorerTopComponent.class).putBoolean("activated", true);
        }
        return cancelled;
    }
}
