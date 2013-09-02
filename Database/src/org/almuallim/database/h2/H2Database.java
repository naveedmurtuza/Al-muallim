/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.database.h2;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.almuallim.database.search.FullTextLuceneEx;
import org.almuallim.service.database.Database;
import org.almuallim.service.helpers.Application;
import org.almuallim.service.search.Column;
import org.almuallim.service.search.SearchCallback;
import org.almuallim.service.search.SearchParameters;
import org.almuallim.service.search.SearchProvider;
import org.almuallim.service.search.SearchResult;
import org.almuallim.service.search.SearchResultFormatter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.NumericUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author Naveed
 */
//@ServiceProviders(value = {
//    ,
//    @ServiceProvider(service = SearchProvider.class)
//})
@ServiceProvider(service = Database.class)
public class H2Database implements Database {

    private static final String DATABASE_NAME = "almuallim";
    private static JdbcConnectionPool connectionPool;
    private Map<String, String> properties;

    @Override
    public Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            connectionPool = JdbcConnectionPool.create(String.format("jdbc:h2:%s%s", getDatabasePath(), DATABASE_NAME), DATABASE_NAME, DATABASE_NAME);
        }
        return connectionPool.getConnection();
    }

    private String getDatabasePath() {
        return Application.getHome() + File.separator + "data" + File.separator;
    }

    @Override
    public void close() throws Exception {
        if (connectionPool != null) {
            connectionPool.dispose();
        }
    }
//
//    public void search(SearchParameters args, SearchCallback callback) {
//        try (Connection connection = getConnection()) {
//            BooleanQuery q = new BooleanQuery();
//            q.add(new TermQuery(new Term("TABLE_NAME", args.getFulltextIndexTableName())), BooleanClause.Occur.MUST);
//            for (Column column : args.getConstraints()) {
//                Query innerQuery = null;
//                Object value = column.getValue();
//                switch (column.getValueType()) {
//                    case IntegerRangeInclusive:
//                        if (value instanceof int[]) {
//                            int[] range = (int[]) value;
//                            innerQuery = NumericRangeQuery.newIntRange(column.getColumnName(), range[0], range[1], true, true);
//                        }
//                        break;
//                    case IntegerRangeExclusive:
//                        if (value instanceof int[]) {
//                            int[] range = (int[]) value;
//                            innerQuery = NumericRangeQuery.newIntRange(column.getColumnName(), range[0], range[1], false, false);
//                        }
//                        break;
//                    case IntegerArray:
//                        if (value instanceof int[]) {
//                            int[] values = (int[]) value;
//                            BooleanQuery boolQuery = new BooleanQuery();
//                            for (int id : values) {
//                                boolQuery.add(new TermQuery(new Term(column.getColumnName(), NumericUtils.intToPrefixCoded(id))), BooleanClause.Occur.SHOULD);
//                            }
//                        }
//                        break;
//                }
//                if (innerQuery != null) {
//                    q.add(innerQuery, BooleanClause.Occur.MUST);
//                }
//            }
//
//            //Query q1 = new TermQuery(new Term("VERSE_ID", NumericUtils.intToPrefixCoded(1))); //NumericRangeQuery.newIntRange("VERSE_ID", 0, 7, true, true);
//
//            ResultSet search = FullTextLuceneEx.search(connection, args.getTerm(), q, args.getLimit(), args.getOffset(), false);
//            while (search.next()) {
//                float score = search.getFloat(2);
//                try (Statement stmt = connection.createStatement()) {
//                    ResultSet rs = stmt.executeQuery("SELECT * FROM" + search.getString(1));
//                    if (rs.next()) {
//                        SearchResultFormatter searchResultFormatter = args.getSearchResultFormatter();
//                        searchResultFormatter.format(rs);
//
//                        SearchResult searchResult = new SearchResult();
//                        searchResult.setDisplayableResult(searchResultFormatter.getDisplayableResult());
//                        searchResult.setReferenceText(searchResultFormatter.getReferenceText());
//                        searchResult.setUrl(searchResultFormatter.getUrl());
//                        searchResult.setTag(searchResultFormatter.getTag());
//                        callback.resultFound(args.getTerm(), score, searchResult);
//                    }
//                }
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
