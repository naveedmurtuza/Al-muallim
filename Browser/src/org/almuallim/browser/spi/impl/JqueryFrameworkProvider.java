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
 * Provides the base javascript files and stylesheets
 * @author Naveed Quadri
 */
@ServiceProvider(service = JavascriptFrameworkProvider.class)
public class JqueryFrameworkProvider implements JavascriptFrameworkProvider {

    private String frameworkCode = null;/*Lazily created*/

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
            frameworkCode = sb.toString();
        }
        return frameworkCode;
    }
}
