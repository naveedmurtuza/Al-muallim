/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.api;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.almuallim.service.database.Database;
import org.almuallim.service.search.SearchDocument;
import org.almuallim.service.search.SearchIndexer;
import org.almuallim.service.url.AlmuallimURL;
import org.almuallim.theholyquran.ModuleConstants;
import org.almuallim.theholyquran.parsers.TanzilParser;
import org.almuallim.theholyquran.parsers.TanzilTextParser;
import org.almuallim.theholyquran.parsers.TanzilXMLParser;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Naveed
 */
public class TheHolyQuran implements ModuleConstants {

    public static String BISMILLAH = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ";
    public static final int VERSE_COUNT = 6236;
    public static final int CHAPTER_COUNT = 114;
    private List<Chapter> chapters;
    private Database database;

    private TheHolyQuran() {
    }

    public static TheHolyQuran getInstance() {
        return TheHolyQuranHolder.INSTANCE;
    }

    private static class TheHolyQuranHolder {

        private static final TheHolyQuran INSTANCE = new TheHolyQuran();
    }

    private Connection getConnection() throws SQLException {
        if (database == null) {
            database = Lookup.getDefault().lookup(Database.class);
        }
        return database.getConnection();

    }

    public TanzilParser detectParser(String path) throws IOException {
        TanzilParser parser;
        if (path.endsWith("xml")) {
            parser = new TanzilXMLParser();
        } else {
            try (FileReader fr = new FileReader(path)) {
                try (PushbackReader pbr = new PushbackReader(fr)) {
                    char ch = (char) pbr.read();
                    if (ch == '1') {
                        parser = new TanzilTextParser(true);
                    } else {
                        parser = new TanzilTextParser(false);
                    }
                }
            }

        }
        return parser;
    }

    private void addDocument(String path, TanzilParser parser, String iso2Lang, boolean rtl, boolean transliteration, String translator, String authorLocalizedName, String bio) throws IOException {
        List<String> verses;

        try (Reader fis = Files.newReader(new File(path), Charsets.UTF_8)) {
            verses = parser.parse(fis);
        }
        if (verses == null) {
            return; //TODO: throw exception
        }
        ArrayList<SearchDocument> sdocs = new ArrayList<>();
        HashMap<String, String> urlParams = new HashMap<>();
        try (Connection connection = getConnection()) {
            int languageId = Language.findByIso2Code(connection, iso2Lang, transliteration);
            if (languageId == -1) {
                languageId = Language.addNew(connection, iso2Lang, rtl, transliteration);
            }
            int translatorId = Translator.findByName(connection, translator);
            if (translatorId == -1) {
                translatorId = Translator.addNew(connection, translator, authorLocalizedName, bio, languageId);
            }
            urlParams.put("translator", "" + translatorId);

            final int batchSize = 5000;
            int count = 0;
            connection.setAutoCommit(false); //keep auto commit false 
            try (PreparedStatement ps = connection.prepareStatement(String.format("INSERT INTO VERSE_I18N (VERSE_TEXT,VERSE_ID,TRANSLATOR_ID,LANG_ID) VALUES (?,?,%d,%d)", translatorId, languageId))) {
                for (int i = 1; i <= verses.size(); i++) {
                    String text = verses.get(i - 1);
                    Chapter ch = getChapterFor(i);
                    int verseInContext = ch.getIndexInContext(i);
                    urlParams.put("chapter", "" + ch.getIndex());
                    urlParams.put("verse", "" + verseInContext);
                    urlParams.put("title", "" + ch.getVerbatimName());
                    ps.setString(1, text);
                    ps.setInt(2, i);
                    ps.addBatch();
                    SearchDocument sd = new SearchDocument(text, String.format("The Holy Quran [Chapter %d: Verse %d]", ch.getIndex(), verseInContext), getURL(urlParams), ModuleConstants.MODULE_NAME);
                    HashMap<String, Object> terms = new HashMap<>();
                    terms.put("verse", new Integer(i));
                    terms.put("translator", translator);
                    sd.setParameters(terms);
                    sdocs.add(sd);
                    if (++count % batchSize == 0) {
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();
                connection.commit();
                SearchIndexer indexer = Lookup.getDefault().lookup(SearchIndexer.class);
                if (indexer != null) {
                    indexer.index(sdocs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getURL(Map<String, String> parameters) {
        String url = "almuallim://" + MODULE_NAME + "?" + AlmuallimURL.ClassNameKey + "=" + URL_OPENER_CLASS_PATH;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            url += "&" + key + "=" + value;
        }
        return url;
    }

    private void addDocumentRowByRow(String path, TanzilParser parser, String iso2Lang, boolean rtl, boolean transliteration, String authorName, String authorLocalizedName, String bio) throws IOException {

        List<String> verses;
        try (Reader fis = Files.newReader(new File(path), Charsets.UTF_8)) {
            verses = parser.parse(fis);
        }
        if (verses == null) {
            return; //TODO: throw exception
        }
        try (Connection connection = getConnection()) {
            int languageId = Language.findByIso2Code(connection, iso2Lang, transliteration);
            if (languageId == -1) {
                languageId = Language.addNew(connection, iso2Lang, rtl, transliteration);
            }
            int authorId = Translator.findByName(connection, authorName);
            if (authorId == -1) {
                authorId = Translator.addNew(connection, authorName, authorLocalizedName, bio, languageId);
            }
            String sql = "INSERT INTO VERSE_I18N (VERSE_TEXT,VERSE_ID,TRANSLATOR_ID,LANG_ID) VALUES ";
            String sql2;
            //StringBuilder sb = new StringBuilder(sql);
            //sb.append("INSERT INTO VERSE_I18N (VERSE_TEXT,VERSE_ID,TRANSLATOR_ID,LANG_ID) VALUES ");

            for (int i = 1; i <= verses.size(); i++) {
                sql2 = sql + String.format("(?,%d,%d,%d)", i, authorId, languageId);
                try (PreparedStatement pstmt = connection.prepareStatement(sql2)) {
                    pstmt.setString(1, verses.get(i - 1));
                    pstmt.executeUpdate();
                }
                //sb.append("(").append("?,").append(i).append(",").append(authorId).append(",").append(languageId).append("),");
            }

        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTranslation(String path, TanzilParser parser, String iso2Lang, boolean rtl, String authorName, String authorLocalizedName, String bio) throws IOException {
        addDocument(path, parser, iso2Lang, rtl, false, authorName, authorLocalizedName, bio);

    }

    public void addTransliteration(String path, TanzilParser parser, String iso2Lang, boolean rtl, String authorName, String authorLocalizedName, String bio) throws IOException {
        addDocument(path, parser, iso2Lang, rtl, true, authorName, authorLocalizedName, bio);
    }

    /**
     * COUNT(*) Optimization If the query only counts all rows of a table, then
     * the data is not accessed. However, this is only possible if no WHERE
     * clause is used, that means it only works for queries of the form SELECT
     * COUNT(*) FROM table.
     */
    public boolean checkDatabaseIntegerity() {
        boolean success = true; // being optimistic ;)
        try (Connection connection = getConnection()) {
            int total = Translator.count(connection);
            int verseCount = 0;
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM VERSE_I18N");
                if (rs.next()) {
                    verseCount = rs.getInt(1);
                }
            }
            if ((verseCount / total) != VERSE_COUNT) {
                success = false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
            success = false;
        }
        return success;
    }

    public List<Translator> getIncompleteTranslations() {
        List<Translator> incompleteTranslators = new ArrayList<>();
        try (Connection connection = getConnection()) {
            List<Translator> translators = Translator.all(connection);
            for (Translator translator : translators) {
                try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM VERSE_I18N WHERE TRANSLATOR_ID = ?")) {
                    pstmt.setInt(1, translator.getId());
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        int verseCount = rs.getInt(1);
                        if (verseCount != VERSE_COUNT) {
                            incompleteTranslators.add(translator);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
        }
        return incompleteTranslators;
    }

    public void deleteTranslation(int id) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM VERSE_I18N WHERE TRANSLATOR_ID = ?")) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteTranslations(int[] ids) {
        for (int id : ids) {
            deleteTranslation(id);
        }
    }

    public VerseCollection previewTranslation(String path, TanzilParser parser) throws IOException {
        List<String> verses = null;
        try (Reader fis = Files.newReader(new File(path), Charsets.UTF_8)) {
            verses = parser.parse(fis);
        }
        if (verses == null) {
            throw new NullPointerException();
        }
        Chapter chapter = getChapterFor(1);
        ArrayList<Verse> list = new ArrayList<>();
        //empty translator => so that isVerbatim is false..
        for (int i = chapter.getStart(); i < chapter.getStart() + chapter.getVerseCount(); i++) {
            list.add(new Verse(i, verses.get(i), chapter, new Translator()));
        }
        return new VerseCollection(list);
    }

    public int getRandomVerseNumber() {
        float nextFloat = new Random().nextFloat();
        return (int) (nextFloat * VERSE_COUNT);
    }

    public Chapter getChapter(int chapterIndex) {
        return getChapters().get(chapterIndex - 1);
    }

    public Chapter getChapterFor(int verse) {
        Chapter ch = null;
        if (verse < 1 || verse > VERSE_COUNT) {
            throw new IllegalArgumentException("Out of range");
        }
        for (Chapter chapter : getChapters()) {
            int startVerse = chapter.getStart();
            int endVerse = startVerse + chapter.getVerseCount();
            if (startVerse < verse && endVerse >= verse) {
                ch = chapter;
                break;
            }
        }
        return ch;
    }

    public List<Chapter> getChapters() {
        if (chapters == null) {
            List<Chapter> temp = new ArrayList<>(114);
            try (Connection connection = getConnection()) {
                try (Statement stmt = connection.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT * FROM CHAPTER ORDER BY INDEX");
                    while (rs.next()) {
                        Chapter chapter = new Chapter();
                        chapter.setIndex(rs.getInt("INDEX"));
                        chapter.setVerbatimName(rs.getString("CHAPTER_NAME"));
                        chapter.setOrder(rs.getInt("REVELATION_ORDER"));
                        chapter.setRukus(rs.getInt("RUKUS"));
                        chapter.setStart(rs.getInt("START_VERSE"));
                        chapter.setVerseCount(rs.getInt("TOTAL_VERSE"));
                        chapter.setType(rs.getString("TYPE"));
                        temp.add(chapter);
                    }
                    chapters = Collections.unmodifiableList(temp);
                }
            } catch (SQLException ex) {
                Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return chapters;
    }

    public Verse translateVerse(int verse, Translator translator) {
        VerseCollection verseCollection = translateVerses(verse, verse, translator);
        return verseCollection.iterator().next();
    }

    public VerseCollection translateVerses(int chapterIndex, Translator translator) {
        Chapter chapter = getChapter(chapterIndex);
        return translateVerses(chapter, translator);

    }

    public VerseCollection translateVerses(int chapterIndex, int translatorId) {
        Chapter chapter = getChapter(chapterIndex);
        return translateVerses(chapter, translatorId);

    }

    public VerseCollection translateVerses(Chapter chapter, Translator translator) {
        List<Verse> verses = getVerses(translator, chapter.getStart() + 1, chapter.getStart() + chapter.getVerseCount() + 1);//coz one based
        return new VerseCollection(verses);
    }

    public VerseCollection translateVerses(Chapter chapter, int translatorId) {
        List<Verse> verses = getVerses(translatorId, chapter.getStart() + 1, chapter.getStart() + chapter.getVerseCount() + 1);//coz one based
        return new VerseCollection(verses);
    }

    public VerseCollection translateVerses(int start, int end, Translator translator) {
        //do some checking
        if (!(start > 0 && end <= VERSE_COUNT)) {
            throw new IllegalArgumentException("Verse out of range");
        }
        List<Verse> verses = getVerses(translator, start, end);

        return new VerseCollection(verses);
    }

    public VerseCollection getVerses(Chapter chapter) {
        return getVerses(chapter.getStart(), chapter.getStart() + chapter.getVerseCount());
    }

    public VerseCollection getVerses(int start, int end) {
        List<Verse> verses = getVerses(null, start, end);
        return new VerseCollection(verses);
    }

    public List<Language> getAvailableLanguagesForChapterNames() {

        List<Language> languages = null;
        try (Connection connection = getConnection()) {
            languages = Language.allInChapterNames(connection);

        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
        }
        return languages;
    }

    public String translateChapterName(int chapterIndex, String iso2langCode, boolean transliterate) {
        String chapterName = null;
        try (Connection connection = getConnection()) {
            int id = Language.findByIso2Code(connection, iso2langCode, transliterate);
            try (PreparedStatement stmt = connection.prepareStatement("SELECT CHAPTER_NAME FROM CHAPTERNAMES WHERE LANG_ID = ? AND CHAPTER_ID = ?")) {
                stmt.setInt(1, id);
                stmt.setInt(2, chapterIndex);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    chapterName = rs.getString("CHAPTER_NAME");
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
        }

        return chapterName;
    }

    public List<String> translateChapterNames(String iso2langCode, boolean transliterate) {
        List<String> chapterNames = new ArrayList<>();
        try (Connection connection = getConnection()) {
            int id = Language.findByIso2Code(connection, iso2langCode, transliterate);
            try (PreparedStatement stmt = connection.prepareStatement("SELECT CHAPTER_NAME FROM CHAPTERNAMES WHERE LANG_ID = ? ORDER BY CHAPTER_ID")) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    chapterNames.add(rs.getString("CHAPTER_NAME"));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
        }

        return chapterNames;
    }

    //http://lucene.apache.org/core/old_versioned_docs/versions/3_0_1/queryparsersyntax.html
    public static void onCreateDatabase(Connection connection) throws SQLException {
        try (Scanner scanner = new Scanner(TheHolyQuran.class.getClassLoader().getResourceAsStream("org/almuallim/theholyquran/data/holyquran.sql"), "utf-8")) {
            try (Statement stmt = connection.createStatement()) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("#")) {
                        continue;
                    }
                    stmt.addBatch(line);
                }
                stmt.executeBatch();
            }
        }
        TanzilParser parser = new TanzilTextParser();
        List<String> verses = parser.parse(new InputStreamReader(TheHolyQuran.class.getClassLoader().getResourceAsStream("org/almuallim/theholyquran/data/quran-simple-enhanced.txt"), Charsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO VERSE (INDEX,VERSE_TEXT) VALUES ");
        for (int i = 1; i <= verses.size(); i++) {
            sb.append("(").append(i).append(",?),");
        }
        try (PreparedStatement stmt = connection.prepareStatement(sb.toString())) {
            for (int i = 1; i <= verses.size(); i++) {
                stmt.setString(i, verses.get(i - 1));
            }
            stmt.executeUpdate();
        }

//        try (Scanner scanner = new Scanner(TheHolyQuran.class.getClassLoader().getResourceAsStream("org/almuallim/theholyquran/data/fulltext.sql"))) {
//            try (Statement stmt = connection.createStatement()) {
//                while (scanner.hasNextLine()) {
//                    String line = scanner.nextLine();
//                    if (line.startsWith("#")) {
//                        continue;
//                    }
//                    stmt.addBatch(line);
//                }
//                stmt.executeBatch();
//            }
//        }
    }

    private List<Verse> getVerses(Connection connection, Translator translator, int start, int end) throws SQLException {
        List<Verse> verses = new ArrayList<>();
        String sql;
        if (translator == null) {
            sql = "SELECT INDEX,VERSE_TEXT FROM VERSE WHERE INDEX BETWEEN ? AND ? ORDER BY INDEX";
        } else {

            sql = "SELECT VERSE_ID,VERSE_TEXT FROM VERSE_I18N WHERE LANG_ID = " + translator.getLanguage().getId() + " AND TRANSLATOR_ID = " + translator.getId() + " AND VERSE_ID BETWEEN ? AND ? ORDER BY VERSE_ID";
        }
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, start);
            pstmt.setInt(2, end);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int verseIndex = rs.getInt(1);
                Verse verse = new Verse(verseIndex, rs.getString(2), getChapterFor(verseIndex), translator);
                verses.add(verse);
            }
        }
        return verses;
    }

    private List<Verse> getVerses(int translatorId, int start, int end) {
        List<Verse> verses = null;
        try (Connection connection = getConnection()) {
            Translator translator = Translator.findById(connection, translatorId);
            verses = getVerses(connection, translator, start, end);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return verses;
    }

    private List<Verse> getVerses(Translator translator, int start, int end) {
        List<Verse> verses = null;
        try (Connection connection = getConnection()) {
            verses = getVerses(connection, translator, start, end);
        } catch (SQLException ex) {
            Logger.getLogger(TheHolyQuran.class.getName()).log(Level.SEVERE, null, ex);
        }
        return verses;
    }
}
