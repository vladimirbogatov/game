package com.game.service;

public enum DefaultValueOfPaging {
    PAGE_NUMBER(0),
    PAGE_SIZE(3);
    private final Integer fieldValue;

    DefaultValueOfPaging(Integer fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Integer getFieldValue() {
        return fieldValue;
    }
}
