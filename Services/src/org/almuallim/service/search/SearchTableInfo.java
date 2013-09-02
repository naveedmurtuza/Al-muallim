/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.service.search;

/**
 *
 * @author Naveed
 */
public class SearchTableInfo {

    private final String schema;
    private final String tableName;
    private final String columns;
    private final String fields;

    public SearchTableInfo(String schema, String tableName, String columns, String fields) {
        this.schema = schema;
        this.tableName = tableName;
        this.columns = columns;
        this.fields = fields;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumns() {
        return columns;
    }

    public String getFields() {
        return fields;
    }
}
