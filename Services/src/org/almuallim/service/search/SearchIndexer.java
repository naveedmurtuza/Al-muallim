/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.service.search;

import java.io.IOException;
import java.util.Collection;

/**
 *
 * @author Naveed
 */
public interface SearchIndexer {
    
    public void index(Collection<SearchDocument> docs) throws IOException;
    
    public void deleteDocument(String key,String value) throws IOException;

}
