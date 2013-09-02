package org.almuallim.lucene.search;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;
import org.almuallim.service.browser.JavascriptFrameworkProvider;
import org.almuallim.service.helpers.FileUtils;
import org.almuallim.service.helpers.HtmlUtils;
import org.almuallim.service.url.AlmuallimURL;
import org.almuallim.service.url.AlmuallimURLOpener;
import org.apache.velocity.VelocityContext;
import org.apache.velocty.api.VelocityModule;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = AlmuallimURLOpener.class)
public class SearchUrlOpener implements AlmuallimURLOpener,ModuleConstants {
    private static final Logger LOG = Logger.getLogger(SearchUrlOpener.class.getName());

    
    @Override
    public void generateHtml(AlmuallimURL url, File file) {
//        if (url.getParameters().isEmpty()) 
        {
            //home page
            JavascriptFrameworkProvider jsFramework = Lookup.getDefault().lookup(JavascriptFrameworkProvider.class);
            
            //get the module specific styles
            File www = InstalledFileLocator.getDefault().locate(PATH_WWW_DIR, MODULE_CODENAME_BASE, false);
            File[] wwwFiles = FileUtils.listFiles(www, ".css;.js");
            String customStyles = HtmlUtils.encloseInTag(wwwFiles);
            //locate the bismillah.svg
            File images = InstalledFileLocator.getDefault().locate(PATH_SLIDESHOW_IMAGES_DIR, MODULE_CODENAME_BASE, false);
            File[] files = images.listFiles();
            StringBuilder sb = new StringBuilder("<script>");
            sb.append("var photos = [");
            for (File f : files) {
                String title = Files.getNameWithoutExtension(f.getName());
                sb.append("{");
                sb.append("\"image\":\"").append(Utilities.toURI(f).toString()).append("\"").append(",");
                sb.append("\"firstline\":\"").append(title).append("\"");
                sb.append("}").append(",");

            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("];");
            sb.append("</script>");
            LOG.info("Generating Search HTML");
            VelocityModule.initialize();
            VelocityContext context = new VelocityContext();
            context.put("images", sb.toString());
            context.put("moduleStyles", customStyles);
            context.put("javascriptFramework", jsFramework.getFramework());

            File templateFile = InstalledFileLocator.getDefault().locate(PATH_TEMPLATE_FILE, MODULE_CODENAME_BASE, false);
            try {
                
                VelocityModule.doMerge(templateFile.getAbsolutePath(), context, java.nio.file.Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
