/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import org.almuallim.service.browser.JavascriptFrameworkProvider;
import org.almuallim.service.helpers.Application;
import org.almuallim.service.helpers.FileUtils;
import org.almuallim.service.helpers.HtmlUtils;
import org.almuallim.service.url.AlmuallimURL;
import org.almuallim.service.url.AlmuallimURLOpener;
import org.almuallim.theholyquran.ModuleConstants;
import org.apache.velocity.VelocityContext;
import org.apache.velocty.api.VelocityModule;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Naveed
 */
@ServiceProvider(service = AlmuallimURLOpener.class)
public class OpenChapter implements AlmuallimURLOpener, ModuleConstants {

    private static final Logger LOG = Logger.getLogger(OpenChapter.class.getName());

    @Override
    public void generateHtml(AlmuallimURL url, File file) {
        TheHolyQuran holyQuran = TheHolyQuran.getInstance();
        Map<String, String> parameters = url.getParameters();
        int chapterIndex = Integer.parseInt(parameters.get("chapter"));
        Chapter chapter = holyQuran.getChapter(chapterIndex);
        JavascriptFrameworkProvider jsFramework = Lookup.getDefault().lookup(JavascriptFrameworkProvider.class);
        //locate the bismillah.svg
        File bismillahSvg = InstalledFileLocator.getDefault().locate(PATH_BISMILLAH_SVG_FILE, MODULE_CODE_NAME_BASE, false);
        File www = InstalledFileLocator.getDefault().locate(PATH_WWW_DIR, MODULE_CODE_NAME_BASE, false);
        File[] files = FileUtils.listFiles(www, ".css;.js");
        String customStyles = HtmlUtils.encloseInTag(files);
        String absPath = Application.getHome() + File.separatorChar + "TheHolyQuran" + File.separatorChar + "verses";

        String[] versePngs = new String[chapter.getVerseCount()];
        for (int i = 1; i <= chapter.getVerseCount(); i++) {
            int index = chapter.getStart() + i;
            versePngs[i - 1] = absPath + File.separatorChar + index + ".png";
        }
        LOG.info("Generating HTML");
        VelocityModule.initialize();
        VelocityContext context = new VelocityContext();
        context.put("javascriptFramework", jsFramework.getFramework());
        context.put("customStyleScripts", customStyles);
        context.put("chapter", chapter);
        context.put("url", url.getUrl());
        context.put("bismillahImagePath", Utilities.toURI(bismillahSvg).toString());
        context.put("verses", getVerses(versePngs, url, chapterIndex));

        File templateFile = InstalledFileLocator.getDefault().locate(PATH_TEMPLATE_FILE, MODULE_CODE_NAME_BASE, false);
        try {

            VelocityModule.doMerge(templateFile.getAbsolutePath(), context, Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public VerseObject[] getVerses(String[] pngs, AlmuallimURL url, int chapterIndex) {
        VerseObject[] verses = new VerseObject[pngs.length];
        for (int i = 0; i < pngs.length; i++) {
            int verseIndex = i + 1;
            url.getParameters().put("verse", "" + verseIndex);
            verses[i] = new VerseObject(Utilities.toURI(new File(pngs[i])).toString(), url.getUrl(), chapterIndex, verseIndex);
        }

        return verses;
    }

    public class VerseObject {

        private final String imagePath;
        private final String url;
        private final int chapterIndex;
        private final int verseIndex;

        public VerseObject(String imagePath, String url, int chapterIndex, int verseIndex) {
            this.imagePath = imagePath;
            this.url = url;
            this.verseIndex = verseIndex;
            this.chapterIndex = chapterIndex;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getUrl() {
            return url;
        }

        public int getChapterIndex() {
            return chapterIndex;
        }

        public int getVerseIndex() {
            return verseIndex;
        }

        public String getRefText() {
            return String.format("[Chapter %d: Verse %d]", chapterIndex, verseIndex);
        }
    }
}
