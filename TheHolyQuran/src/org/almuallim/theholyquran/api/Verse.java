/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.api;



/**
 *
 * @author Naveed
 */
public class Verse {

    private final String text;
    private final Chapter chapter;
    private final Translator translator;
    private final int index;

    
    public Verse(int index, String text, Chapter chapter, Translator translator) {
        this.text = text;
        this.chapter = chapter;
        this.translator = translator;
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public Translator getTranslator() {
        return translator;
    }

    public boolean isVerbatim() {
        return translator == null;
    }

    public int getIndexInContext() {
        return index - chapter.getStart();
    }

    public boolean isSajdaVerse() {
        boolean yes = false;
        if (chapter.hasSajda()) {
            Sajda sajda = chapter.getSajdaInfo();

            for (int i = 0; i < sajda.getSajda().length; sajda.getSajda()) {
                if (getIndexInContext() == sajda.getSajda()[i]) {
                    yes = true;
                    break;
                }
            }

        }
        return yes;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return String.format("[%d:%d]", chapter.getIndex(), getIndexInContext());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Verse) {
            return ((Verse) obj).getIndex() == getIndex();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.index;
        return hash;
    }
}
