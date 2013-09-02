/*
 * In the name of Allah, Most Gracious, Most Merciful.
 * 
 */
package org.almuallim.theholyquran.nodes;

import java.util.List;
import javax.swing.SwingUtilities;
import org.almuallim.theholyquran.api.Chapter;
import org.almuallim.theholyquran.api.TheHolyQuran;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Naveed Quadri
 */
public class ChapterCollection extends ChildFactory<Chapter> {


    public ChapterCollection() {
    }

    @Override
    protected boolean createKeys(List<Chapter> toPopulate) {
        toPopulate.addAll(TheHolyQuran.getInstance().getChapters());
        return true;
    }

    @Override
    protected Node createNodeForKey(Chapter key) {
        return new ChapterNode(key);
    }
}
