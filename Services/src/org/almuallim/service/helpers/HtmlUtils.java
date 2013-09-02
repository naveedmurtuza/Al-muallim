package org.almuallim.service.helpers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 *
 * @author Naveed Quadri
 */
public class HtmlUtils {

    public static final int SCRIPT = 1;
    public static final int CSS_LINK = 2;

    private static String encloseInTag(String url, int tag) {
        switch (tag) {
            case SCRIPT:
                return String.format("<script type=\"text/javascript\" src=\"%s\"></script>", url, tag);
            case CSS_LINK:
                return String.format("<link type=\"text/css\" rel=\"stylesheet\" href=\"%s\" />", url, tag);
        }
        return "";
    }

    /**
     * Encloses the given path into matching HTML Tags. <pre>(Eg. link for css and script for js)</pre>
     * @param path
     * @return 
     */
    public static String encloseInTag(Path path) {
        int type = 0;
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            type = path.toFile().getName().endsWith(".js") ? SCRIPT : CSS_LINK;
        }
        return encloseInTag(path.toUri().toString(), type);
    }
    
    /**
     * Encloses each file in the given FIle[] into matching HTML Tags. <pre>(Eg. link for css and script for js)</pre>
     * @param File[] 
     * @return 
     */
    public static String encloseInTag(File[] files) {
        StringBuilder sb = new StringBuilder();
        for (File file : files) {
            sb.append(encloseInTag(file.toPath()));
        }
        return sb.toString();
    }
}
