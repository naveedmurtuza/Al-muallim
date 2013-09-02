/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.api;

import java.util.HashMap;

/**
 *
 * @author Naveed
 */
public class Chapter implements Comparable<Chapter> {

    private static final HashMap<Integer, Sajda> sajdas = new HashMap<>();

    static {

        sajdas.put(7, new Sajda(new int[]{206}, "recommended"));
        sajdas.put(13, new Sajda(new int[]{15}, "recommended"));
        sajdas.put(16, new Sajda(new int[]{50}, "recommended"));
        sajdas.put(17, new Sajda(new int[]{109}, "recommended"));
        sajdas.put(19, new Sajda(new int[]{58}, "recommended"));
        sajdas.put(22, new Sajda(new int[]{18, 77}, "recommended"));
        sajdas.put(25, new Sajda(new int[]{60}, "recommended"));
        sajdas.put(27, new Sajda(new int[]{26}, "recommended"));
        sajdas.put(32, new Sajda(new int[]{15}, "obligatory"));
        sajdas.put(38, new Sajda(new int[]{24}, "recommended"));
        sajdas.put(41, new Sajda(new int[]{38}, "obligatory"));
        sajdas.put(53, new Sajda(new int[]{62}, "obligatory"));
        sajdas.put(84, new Sajda(new int[]{21}, "recommended"));
        sajdas.put(96, new Sajda(new int[]{19}, "obligatory"));
    }

    static boolean isValidChapterIndex(int chapterIndex) {
        return chapterIndex >= 1 && chapterIndex <= 114;
    }

    static boolean isValidVerseRange(Chapter chapter, int start, int end) {
        return chapter.getStart() < start && (chapter.getStart() + chapter.getVerseCount()) <= end;
        //return end <= chapter.getVerseCount();
    }
    private int index;
    private int verseCount;
    private int start;
    private String type;
    private int order;
    private int rukus;
    private String verbatimName;

    public boolean hasSajda() {
        return sajdas.containsKey(index);
    }

    public Sajda getSajdaInfo() {
        return sajdas.get(index);
    }

    public String getVerbatimName() {
        return verbatimName;
    }

    public boolean startsWithBismillah() {
        if (index != 9) {
            return true;
        } else {
            return false;
        }
    }

    public void setVerbatimName(String verbatimName) {
        this.verbatimName = verbatimName;
    }

    public int getIndex() {
        return index;
    }

    public int getIndexInContext(int verseIndex) {
        return verseIndex - getStart();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getVerseCount() {
        return verseCount;
    }

    public void setVerseCount(int verseCount) {
        this.verseCount = verseCount;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getRukus() {
        return rukus;
    }

    public void setRukus(int rukus) {
        this.rukus = rukus;
    }

    @Override
    public int compareTo(Chapter o) {
        return getIndex() - o.getIndex();

    }
}
