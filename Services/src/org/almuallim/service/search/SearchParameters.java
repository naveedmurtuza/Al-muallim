package org.almuallim.service.search;

public class SearchParameters {

    private final String term;
    private final Column[] constraints;
    private final int offset;
    private final int limit;
    private final String fulltextIndexTableName;
    private final SearchResultFormatter searchResultFormatter;

    public SearchParameters(String term, Column[] constraints, int offset, int limit, SearchResultFormatter searchResultFormatter, String fulltextIndexTableName) {
        this.term = term;
        this.constraints = constraints;
        this.offset = offset;
        this.limit = limit;
        this.fulltextIndexTableName = fulltextIndexTableName;
        this.searchResultFormatter = searchResultFormatter;
    }

    public String getFulltextIndexTableName() {
        return fulltextIndexTableName;
    }

    public SearchResultFormatter getSearchResultFormatter() {
        return searchResultFormatter;
    }

    public String getTerm() {
        return term;
    }

    public Column[] getConstraints() {
        return constraints;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
