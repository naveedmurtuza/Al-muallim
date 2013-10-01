/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.api;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Naveed
 */
public class VerseCollection implements Collection<Verse>, Serializable {

    private static final long serialVersionUID = 1820017752578914078L;
    private final Collection<Verse> c;
    private final Translator translator;

    VerseCollection(Collection<Verse> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        this.c = c;
        this.translator = c.iterator().hasNext() ? c.iterator().next().getTranslator() : null;
    }

    public static VerseCollection singleVerseCollection(Verse verse) {
        List<Verse> l = new ArrayList<>();
        l.add(verse);
        return new VerseCollection(l);
    }

    public Translator getTranslator() {
        return translator;
    }

    public boolean isVerbatim() {
        return translator == null;
    }

    @Override
    public int size() {
        return c.size();
    }

    @Override
    public boolean isEmpty() {
        return c.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return c.contains(o);
    }

    @Override
    public Object[] toArray() {
        return c.toArray();
    }

    @Override
    public <Verse> Verse[] toArray(Verse[] a) {
        return c.toArray(a);
    }

    @Override
    public Iterator<Verse> iterator() {
        return new Iterator<Verse>() {
            private final Iterator<? extends Verse> i = c.iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public Verse next() {
                return i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean add(Verse e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> coll) {
        return c.containsAll(coll);
    }

    @Override
    public boolean addAll(Collection<? extends Verse> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(toJSONVerseCollection(this), JSONVerseCollection.class);

    }

    private JSONVerseCollection toJSONVerseCollection(VerseCollection collection) {
        String translatr = collection.getTranslator().toString();
        int id = collection.getTranslator().getId();
        String lang = collection.getTranslator().getLanguage().getIso2Code();
        String text_direction = collection.getTranslator().getLanguage().getTextDirection() == Language.TextDirection.RightToLeft ? "rtl" : "ltr";
        String[] verses = new String[collection.size()];
        int index = 0;
        for (Verse verse : collection) {
            verses[index++] = (verse.getText());
        }
        return new JSONVerseCollection(translatr, lang, id, text_direction, verses);
    }

    private class JSONVerseCollection {

        private String translator;
        private String language;
        private int translator_id;
        private String text_align;
        private String[] verses;

        public JSONVerseCollection(String translator, String language, int translator_id, String text_align, String[] verses) {
            this.translator = translator;
            this.translator_id = translator_id;
            this.text_align = text_align;
            this.verses = verses;
            this.language = language;
        }
    }
}
