package com.osbcp.cssparser;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Naveed Quadri
 */
public class Rules {

    public static void addOrUpdate(List<Rule> rules, Rule rule) {
        for (Selector selector : rule.getSelectors()) {
            for (Iterator<Rule> it = rules.iterator(); it.hasNext();) {
                Rule r = it.next();
                if (r.getSelectors().contains(selector)) {
                    it.remove();
                }
            }
        }
        rules.add(rule);
    }

    public static void save(List<Rule> rules, String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Rule rule : rules) {
            sb.append(implodeSelectors(rule.getSelectors()));
            sb.append("{");
            sb.append(implodeProperties(rule.getPropertyValues()));
            sb.append("}");
        }
        ArrayList<String> lines = new ArrayList<>();
        lines.add(sb.toString());
        File file = new File(path);
        
        Files.write(sb, new File(path), Charset.defaultCharset());
        //Files.write(new File(path).toPath(), lines, Charset.defaultCharset(), StandardOpenOption.CREATE,StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING );
    }

    private static String implodeProperties(final Set<PropertyValue> values) {
        StringBuilder sb = new StringBuilder();
        for (PropertyValue propertyValue : values) {
            sb.append(propertyValue.getProperty()).append(" : ").append(propertyValue.getValue()).append(";");
        }
        return sb.toString();
    }

    private static String implodeSelectors(final Set<Selector> values) {

        StringBuilder sb = new StringBuilder();

        Iterator<Selector> iterator = values.iterator();

        while (iterator.hasNext()) {

            Selector selector = iterator.next();

            sb.append(selector.toString());

            if (iterator.hasNext()) {
                sb.append(", ");
            }

        }

        return sb.toString();

    }
}
