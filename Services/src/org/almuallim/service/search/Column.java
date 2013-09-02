package org.almuallim.service.search;

public class Column {

    private String columnName;
    private Object value;
    private Clause clause;
    private ValueType valueType;

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public Clause getClause() {
        return clause;
    }

    public void setClause(Clause val) {
        this.clause = val;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String val) {
        this.columnName = val;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object val) {
        this.value = val;
    }
}
