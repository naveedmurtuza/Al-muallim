package org.almuallim.service.search;

import java.sql.ResultSet;

/**
 *
 * @author Naveed
 */
public interface SearchResultFormatter {

    public String getDisplayableResult();

    public String getReferenceText();

    public String getUrl();

    public Object getTag();

    public void format(ResultSet rs);
}
