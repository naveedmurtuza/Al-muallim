package org.almuallim.service.helpers;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Naveed
 */
public class FileUtils {

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Convinience method to list files from directory. This method makes nno
     * checks whether the given path is directory or file. the extension passed in should
     * be lower case and separated with ';'
     * @param path path to directory
     * @param ext ; separated list of valid extension lower case
     * @return File[] containing valid files
     */
    public static File[] listFiles(File path, String ext) {
        final String[] exts = ext.split(";");
        return path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowerCasedName = name.toLowerCase();
                for (String extension : exts) {
                    if(lowerCasedName.endsWith(extension))
                        return true;
                }
                return false;
            }
        });
    }
}
