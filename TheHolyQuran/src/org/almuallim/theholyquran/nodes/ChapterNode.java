/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.nodes;

import com.google.common.base.Strings;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.almuallim.service.browser.Browser;
import org.almuallim.service.url.AlmuallimURL;
import org.almuallim.theholyquran.api.Chapter;
import org.almuallim.theholyquran.api.Sajda;
import org.almuallim.theholyquran.api.TheHolyQuran;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Naveed
 */
public class ChapterNode extends AbstractNode implements PreferenceChangeListener {

    private Chapter chapter;
    private final InstanceContent ic;
    private OpenAction openAction;

    public ChapterNode(Chapter chapter) {
        this(chapter, new InstanceContent());
    }

    public ChapterNode(Chapter chapter, InstanceContent ic) {
        super(Children.LEAF, new AbstractLookup(ic));
        this.ic = ic;
        this.chapter = chapter;
        //get the display language
        setDisplayName(chapter.getVerbatimName());
        NbPreferences.forModule(getClass()).addPreferenceChangeListener(this);
    }

    @Override
    public Action getPreferredAction() {
        return getAction();
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{getAction()};
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        
        //<editor-fold defaultstate="collapsed" desc="Chapter Props">
        Sheet.Set chapterProps = Sheet.createPropertiesSet();
        chapterProps.setDisplayName("Chapter"); //NOI18N
        chapterProps.setName("Chapter Properties"); //NOI18N
        chapterProps.setDisplayName("Chapter"); //NOI18N
        Property<Integer> index = new PropertySupport.ReadOnly<Integer>("chapterIndex", Integer.class, "Index", "Index") {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return chapter.getIndex();
            }
        };
        chapterProps.put(index);
        
        Property<String> chapterName = new PropertySupport.ReadOnly<String>("chapterName", String.class, "Chapter Name", "Chapter Name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return chapter.getVerbatimName();
            }
        };
        chapterProps.put(chapterName);
        
        Property<Integer> rukus = new PropertySupport.ReadOnly<Integer>("rukus", Integer.class, "Number of Rukus", "Number of Rukus") {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return chapter.getRukus();
            }
        };
        chapterProps.put(rukus);
        
        sheet.put(chapterProps);
        //</editor-fold>

        Sheet.Set revelationProps = Sheet.createPropertiesSet();
        revelationProps.setDisplayName("Revelation"); //NOI18N
        revelationProps.setName("Revelation Properties"); //NOI18N
        Property<Integer> orderOfRevelation = new PropertySupport.ReadOnly<Integer>("revelationOrder", Integer.class, "Revelation Order", "Revelation Order") {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return chapter.getOrder();
            }
        };
        revelationProps.put(orderOfRevelation);
        Property<String> type = new PropertySupport.ReadOnly<String>("type", String.class, "Type", "Type") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return chapter.getType();
            }
        };
        revelationProps.put(type);
        sheet.put(revelationProps);
        
        Sheet.Set ayaProps = Sheet.createPropertiesSet();
        ayaProps.setDisplayName("Ayas"); //NOI18N
        ayaProps.setName("Aya Properties"); //NOI18N
        Property<Integer> startVerse = new PropertySupport.ReadOnly<Integer>("startVerse", Integer.class, "Start Verse", "Start Verse") {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return chapter.getStart() + 1;
            }
        };
        ayaProps.put(startVerse);
        Property<Integer> totalVerse = new PropertySupport.ReadOnly<Integer>("totalVerse", Integer.class, "Total Verse", "Total Verse") {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return chapter.getVerseCount();
            }
        };
        ayaProps.put(totalVerse);
        sheet.put(ayaProps);

        if (chapter.hasSajda()) {
            Sheet.Set sajdaProps = Sheet.createPropertiesSet();
            sajdaProps.setDisplayName("Sajda"); //NOI18N
            sajdaProps.setName("Sajda Properties"); //NOI18N
            final Sajda sajda = chapter.getSajdaInfo();
            Property<String> sajdaType = new PropertySupport.ReadOnly<String>("sajdaType", String.class, "Sajda Type", "Sajda Type") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return sajda.getType().toLowerCase();
                }
            };
            Property<Integer> sajdaVerse = new PropertySupport.ReadOnly<Integer>("sajdaVerse_", Integer.class, "Verse", "Verse") {
                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return sajda.getSajda()[0];
                }
            };
            sajdaProps.put(sajdaType);
            sajdaProps.put(sajdaVerse);
            sheet.put(sajdaProps);
        }
        return sheet;
    }

    private AlmuallimURL getURL() throws MalformedURLException {
        return new AlmuallimURL("almuallim://TheHolyQuran?className=org.almuallim.theholyquran.api.OpenChapter&chapter=" + chapter.getIndex() + "&title=" + chapter.getVerbatimName());
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        if ("LANGUAGE_CHANGED".equals(evt.getKey())) {
            String value = evt.getNewValue();
            String[] vals = value.split(":");
            String iso2Code = vals[0];
            String displayName;
            if (iso2Code.toLowerCase().contains("verbatim")) {
                displayName = chapter.getVerbatimName();
            } else {
                boolean transliterate = false;
                if (vals.length == 2) {
                    transliterate = !Strings.isNullOrEmpty(vals[1]);
                }
                displayName = TheHolyQuran.getInstance().translateChapterName(chapter.getIndex(), iso2Code, transliterate);
            }
            if (!Strings.isNullOrEmpty(displayName)) {
                setDisplayName(displayName);
                NbPreferences.forModule(Browser.class).put(Browser.PROP_TITLE_CHANGED, displayName);
            }
        }
    }

    private Action getAction() {
        if (openAction == null) {
            openAction = new OpenAction();
        }
        return openAction;
    }

    private class OpenAction extends AbstractAction {

        public OpenAction() {
            putValue(NAME, "Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                AlmuallimURL url = getURL();
                ic.add(url);
                ic.remove(url);
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
