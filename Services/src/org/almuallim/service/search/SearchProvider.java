package org.almuallim.service.search;

import java.io.IOException;

public interface SearchProvider {

    public void search(String args, SearchCallback callback,int pageNumber) throws Exception;
}
