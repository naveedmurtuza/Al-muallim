/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.almuallim.service.helpers.LanguageUtils;

/**
 *
 * @author Naveed
 */
public class Language {

    public static int findByIso2Code(Connection connection, String iso2langCode, boolean transliteration) throws SQLException {
        int id = -1;

        try (PreparedStatement stmt = connection.prepareStatement("SELECT ID FROM LANGUAGE WHERE ISO2NAME = ? AND IS_TRANSLITERATION = ?")) {
            stmt.setString(1, iso2langCode);
            stmt.setString(2, transliteration ? "Y" : "N");

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("ID");
            }
        }

        return id;
    }

    public static int addNew(Connection connection, String iso2langCode, boolean rtl, boolean transliteration) throws SQLException {
        int id = -1;

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO LANGUAGE (ISO2NAME,IS_TRANSLITERATION,TEXT_DIR) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, iso2langCode);
            stmt.setString(2, transliteration ? "Y" : "N");
            stmt.setString(3, rtl ? "RTL" : "LTR");
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            } else {
                // Throw exception?
            }
        }

        return id;
    }

    public static List<Language> allInChapterNames(Connection connection) throws SQLException {
        List<Language> languages = new ArrayList<>();
        languages.add(Language.findById(connection, 1));
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT LANG_ID FROM CHAPTERNAMES ");
            while (rs.next()) {
                Language l = Language.findById(connection, rs.getInt("LANG_ID"));
                languages.add(l);
            }
            //add the arabic-verbatim language
        }

        return languages;
    }

    public static List<Language> all(Connection connection) throws SQLException {
        List<Language> languages = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM LANGUAGE");
            while (rs.next()) {
                Language l = new Language(rs.getInt("ID"));
                l.setIso2Code(rs.getString("ISO2NAME"));
                l.setLocale(rs.getString("LOCALE"));
                l.setTextDirection(rs.getString("TEXT_DIR").equals("RTL") ? TextDirection.RightToLeft : TextDirection.LeftToRight);
                l.setTransliteration("Y".equals(rs.getString("IS_TRANSLITERATION")));
                languages.add(l);
            }
        }

        return languages;
    }

    static Language findById(Connection connection, int aInt) throws SQLException {
        Language l = null;

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM LANGUAGE WHERE ID = ?")) {
            stmt.setInt(1, aInt);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                l = new Language(rs.getInt("ID"));
                l.setIso2Code(rs.getString("ISO2NAME"));
                l.setLocale(rs.getString("LOCALE"));
                l.setTextDirection(rs.getString("TEXT_DIR").equals("RTL") ? TextDirection.RightToLeft : TextDirection.LeftToRight);
                l.setTransliteration("Y".equals(rs.getString("IS_TRANSLITERATION")));
            }
        }

        return l;
    }
    private int id;
    private String iso2Code;
    private String locale;
    private boolean transliteration;
    private TextDirection textDirection;

    public Language(int id) {
        this.id = id;
    }

    public void setIso2Code(String iso2Code) {
        this.iso2Code = iso2Code;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setTransliteration(boolean transliteration) {
        this.transliteration = transliteration;
    }

    public void setTextDirection(TextDirection textDirection) {
        this.textDirection = textDirection;
    }

    public int getId() {
        return id;
    }

    public String getIso2Code() {
        return iso2Code;
    }

    public String getLocale() {
        return locale;
    }

    public boolean isTransliteration() {
        return transliteration;
    }

    public TextDirection getTextDirection() {
        return textDirection;
    }

    public enum TextDirection {

        RightToLeft, LeftToRight
    }

    @Override
    public String toString() {
        //see if its verbatim
        if ("VERBATIM".equals(iso2Code)) {
            return "Arabic (Verbatim)";
        }
        return isTransliteration() ? LanguageUtils.getLanguageName(iso2Code) + "( Transliteration )" : LanguageUtils.getLanguageName(iso2Code);
    }
}
