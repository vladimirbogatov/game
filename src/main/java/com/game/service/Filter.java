package com.game.service;


import java.util.List;

//https://reflectoring.io/spring-data-specifications/

public class Filter {
    private String field;
    private QueryOperator operator;
    private String value;
    private List<String> values; // use in case IN or BETWEEN operator
                                    // 0 - min
                                    // 1 - max

    public Filter(String field, QueryOperator operator, String value, List<String> values) {
        this.field = field;
        this.operator = operator;
        this.value = value;
        this.values = values;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public QueryOperator getOperator() {
        return operator;
    }

    public void setOperator(QueryOperator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
