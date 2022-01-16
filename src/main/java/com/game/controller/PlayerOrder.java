package com.game.controller;

public enum PlayerOrder {
    ID("id"), // default
    NAME("name"),
    TITLE("title"),
    RACE("race"),
    PROFESSION("profession"),
    EXPERIENCE("experience"),
    MIN_EXPERIENCE("minExperience"),
    MAX_EXPERIENCE("maxExperience"),
    LEVEL("level"),
    MIN_LEVEL("minLevel"),
    MAX_LEVEL("maxLevel"),
    UNTIL_NEX_LEVEL("untilNextLevel"),
    BIRTHDAY("birthday"),
    BANNED("banned"),
    AFTER("after"),
    BEFORE("before"),
    PAGE_NUMBER("pageNumber"),
    PAGE_SIZE("pageSize"),
    PLAYER_ORDER("order");

    private final String fieldName;

    PlayerOrder(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}