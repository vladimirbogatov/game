package com.game.service;

import java.util.List;

public final class FilterBuilder {
    private String field;
    private QueryOperator operator;
    private String value;
    private List<String> values; // use in case IN operator

    private FilterBuilder() {
    }

    public static FilterBuilder aFilter() {
        return new FilterBuilder();
    }

    public FilterBuilder withField(String field) {
        this.field = field;
        return this;
    }

    public FilterBuilder withOperator(QueryOperator operator) {
        this.operator = operator;
        return this;
    }

    public FilterBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public FilterBuilder withValues(List<String> values) {
        this.values = values;
        return this;
    }

    public Filter build() {
        return new Filter(field, operator, value, values);
    }
}
