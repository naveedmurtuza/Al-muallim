/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.service.search;

import java.util.UUID;

/**
 *
 * @author Naveed
 */
public interface Searchable {

    public SearchTableInfo[] getSearchTableInfos();

    public String getModuleName();

    public UUID getModuleId();
}
