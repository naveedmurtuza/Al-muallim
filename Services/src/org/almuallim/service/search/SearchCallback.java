package org.almuallim.service.search;


public interface SearchCallback {

    public void resultFound (String term, float score, SearchResult result);

}

