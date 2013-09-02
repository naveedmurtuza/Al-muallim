
/*
 *In the name of Allah, Most Gracious, Most Merciful.
 */
package org.apache.velocty.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Naveed Quadri
 */
public class VelocityModule {

    public static void initialize() {
        /*
         * now create a new VelocityEngine instance, and configure it to use the
         * logger
         */
        Logger log = Logger.getLogger(VelocityModule.class.getName());
        log.info("Starting Velocity Engine");
        //VelocityEngine ve = new VelocityEngine();

        //Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,"org.apache.velocity.runtime.log.JdkLogChute");

        //ve.setProperty("runtime.log.logsystem.log4j.logger",
        //      "VelocityLogger");
        //ve.init();
        log.info("follows initialization output from velocity");
        Properties p = new Properties();
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.JdkLogChute");
        p.setProperty("file.resource.loader.path","" );//FileUtil.toFile(FileUtil.getConfigRoot()).getAbsolutePath()
        p.setProperty("input.encoding", "UTF-8");
        p.setProperty("output.encoding", "UTF-8");

        Velocity.init(p);
    }

    /**
     * 
     * @param templateFile
     * @param context Conext with data elements accessed by template
     * @param os output stream for rendered template. Output stream is closed after use.
     * @throws IOException
     */
    public static void doMerge(String templateFile, VelocityContext context, OutputStream os) throws IOException {
        Template template = Velocity.getTemplate(templateFile, "UTF-8");
        try (OutputStreamWriter writer = new OutputStreamWriter(os, Charset.forName("UTF-8"))) {
            if (template != null) {
                template.merge(context, writer);
            }

            /*
             * flush and cleanup
             */

            writer.flush();
            os.close();
        }
    }
}
