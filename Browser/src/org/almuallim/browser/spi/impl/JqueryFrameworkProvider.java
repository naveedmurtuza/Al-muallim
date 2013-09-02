/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.browser.spi.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.almuallim.service.browser.JavascriptFrameworkProvider;
import org.almuallim.service.helpers.HtmlUtils;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = JavascriptFrameworkProvider.class)
public class JqueryFrameworkProvider implements JavascriptFrameworkProvider {

    private String frameworkCode = null;

    @Override
    public String getFramework() {
        if (frameworkCode == null) {
            File www = InstalledFileLocator.getDefault().locate("www", "org.almuallim.browser.", false);
            final StringBuilder sb = new StringBuilder();
            try {
                Files.walkFileTree(www.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(
                            Path aFile, BasicFileAttributes aAttrs) throws IOException {
                        sb.append(HtmlUtils.encloseInTag(aFile));
                        sb.append(System.lineSeparator());
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult preVisitDirectory(
                            Path aDir, BasicFileAttributes aAttrs) throws IOException {
                        //exclude images, fonts
                        if (aDir.toFile().getName().startsWith("fonts") || aDir.toFile().getName().startsWith("images")) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
//            StringBuilder sb = new StringBuilder();
//            sb.append(encloseInTag(getClass().getResource("files/js/jquery.min.1.9.0.js").toExternalForm(), "script"));
//            sb.append(encloseInTag(getClass().getResource("files/js/jquery.contextmenu.js").toExternalForm(), "script"));
//            sb.append(encloseInTag(getClass().getResource("files/js/jquery.toolbar.js").toExternalForm(), "script"));
//            sb.append(encloseInTag(getClass().getResource("files/js/jquery.sticky.js").toExternalForm(), "script"));
//            sb.append(encloseInTag(getClass().getResource("files/js/main.js").toExternalForm(), "script"));
//            sb.append(encloseInTag(getClass().getResource("files/css/jquery-ui.css").toExternalForm(), "link"));
//            sb.append(encloseInTag(getClass().getResource("files/css/jquery.contextmenu.css").toExternalForm(), "link"));
////            sb.append(encloseInTag(getClass().getResource("files/css/jquery.toolbar.css").toExternalForm(), "link"));
//            sb.append(encloseInTag(getClass().getResource("files/css/main.css").toExternalForm(), "link"));
            frameworkCode = sb.toString();
        }
        return frameworkCode;
    }
}
