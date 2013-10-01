/*
 *In the name of Allah, Most Gracious, Most Merciful.
 */
package org.almuallim.service.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Naveed Quadri
 */
public class ZipUtils {

    public static void extract(InputStream is, String pathToExtract) throws IOException {
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String currentEntry = entry.getName();
            File destFile = new File(pathToExtract, currentEntry);
            File destParent = destFile.getParentFile();
            destParent.mkdirs();
            if (!entry.isDirectory()) {
                int currentByte;
                byte[] buffer = new byte[512];
                try (FileOutputStream fos = new FileOutputStream(destFile); BufferedOutputStream bos = new BufferedOutputStream(fos, 512)) {
                    while ((currentByte = zis.read(buffer, 0, 512)) != -1) {
                        bos.write(buffer, 0, currentByte);
                    }
                }
                zis.closeEntry();
            }
        }
    }
}
