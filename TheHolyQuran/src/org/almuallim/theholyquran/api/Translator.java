/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.theholyquran.api;

import com.google.common.base.Strings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Naveed
 */
public class Translator {

    public static int findByName(Connection connection, String authorName) throws SQLException {
        int id = -1;

        try (PreparedStatement stmt = connection.prepareStatement("SELECT ID FROM TRANSLATOR WHERE NAME = ?")) {
            stmt.setString(1, authorName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("ID");
            }
        }

        return id;
    }

    public static int addNew(Connection connection, String authorName, String authorLocalizedName, String bio, int langId) throws SQLException {
        int id = -1;

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO TRANSLATOR (NAME,LOCALIZED_NAME,LANG_ID) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, authorName);
            stmt.setString(2, authorLocalizedName);
            stmt.setInt(3, langId);
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

    public static int count(Connection connection) throws SQLException {
        int count = 0;
        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TRANSLATOR");
            while (rs.next()) {
                count = rs.getInt(1);
            }
        }
        return count;
    }

    public static List<Translator> all(Connection connection) throws SQLException {
        List<Translator> translators = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM TRANSLATOR");
            while (rs.next()) {
                Translator l = new Translator();
                l.setId(rs.getInt("ID"));
                l.setBio(rs.getString("INTRODUCTION"));
                l.setLanguage(Language.findById(connection, rs.getInt("LANG_ID")));
                l.setLocalizedName(rs.getString("LOCALIZED_NAME"));
                l.setName(rs.getString("NAME"));
                translators.add(l);
            }
        }

        return translators;
    }

    public static Translator findById(Connection connection, int id) throws SQLException {
        Translator translator = null;
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM TRANSLATOR WHERE ID = ?")) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                translator = new Translator();
                translator.setId(rs.getInt("ID"));
                translator.setBio(rs.getString("INTRODUCTION"));
                translator.setLanguage(Language.findById(connection, rs.getInt("LANG_ID")));
                translator.setLocalizedName(rs.getString("LOCALIZED_NAME"));
                translator.setName(rs.getString("NAME"));
            }
        }
        return translator;
    }

    public static List<Translator> forLanguage(Connection connection, Language language) throws SQLException {
        List<Translator> translators = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM TRANSLATOR WHERE LANG_ID = ?")) {
            stmt.setInt(1, language.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Translator translator = new Translator();
                translator.setId(rs.getInt("ID"));
                translator.setBio(rs.getString("INTRODUCTION"));
                translator.setLanguage(language);
                translator.setLocalizedName(rs.getString("LOCALIZED_NAME"));
                translator.setName(rs.getString("NAME"));
                translators.add(translator);
            }
        }

        return translators;
    }
    private int id;
    private String name;
    private String localizedName;
    private Language language;
    private String bio;

    public Translator() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return Strings.isNullOrEmpty(localizedName) ? name : localizedName;
    }
}
