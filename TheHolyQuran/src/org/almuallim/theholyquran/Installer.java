/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran;

import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import org.almuallim.service.helpers.Application;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

public class Installer extends ModuleInstall {

    private static final Logger LOG = Logger.getLogger(Installer.class.getName());

//    public void restored() {
//       //C:\Users\Naveed\Almuallim\data
//        NbPreferences.forModule(getClass()).putBoolean("activated", false);
//        try {
//            NbPreferences.forModule(getClass()).flush();
//        } catch (BackingStoreException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        LOG.warning("Forcing activated to true in installer");
//    }
    //    @Override
    //    public void restored() {
    //        boolean activated = NbPreferences.forModule(getClass()).getBoolean("activated", false);
    //        if (!activated) {
    //            Database database = Lookup.getDefault().lookup(Database.class);
    //            try (Connection connection = database.getConnection()) {
    //                //drop all tables
    //                try (Statement stmt = connection.createStatement()) {
    //                    stmt.addBatch("DROP TABLE IF EXISTS CHAPTER");
    //                    stmt.addBatch("DROP TABLE IF EXISTS LANGUAGE");
    //                    stmt.addBatch("DROP TABLE IF EXISTS CHAPTERNAMES");
    //                    stmt.addBatch("DROP TABLE IF EXISTS TRANSLATOR");
    //                    stmt.addBatch("DROP TABLE IF EXISTS VERSE");
    //                    stmt.addBatch("DROP TABLE IF EXISTS VERSE_I18N");
    //                    stmt.executeBatch();
    //                }
    //                try (Statement stmt = connection.createStatement()) {
    //                    String path;
    //                    try (ResultSet rs = stmt.executeQuery("CALL DATABASE_PATH()")) {
    //                        rs.next();
    //                        path = rs.getString(1);
    //                        int index = path.lastIndexOf(':');
    //                        if (index > 1) {
    //                            path = path.substring(index + 1);
    //                        }
    //                    }
    //                    //check lucene index
    //                    FileUtils.deleteDirectory(new File(path));
    //                }
    //                TheHolyQuran.onCreateDatabase(connection);
    //                //all done
    //                NbPreferences.forModule(getClass()).putBoolean("activated", true);
    //                NbPreferences.forModule(getClass()).flush();
    //            } catch (SQLException sqle) {
    //                LOG.log(Level.SEVERE, "Error activating module", sqle);
    //                //TODO:// show dialog
    //            } catch (BackingStoreException bse) {
    //                LOG.log(Level.WARNING, "Error activating module", bse);
    //            }
    //
    //        }
    //    }
//    
}
