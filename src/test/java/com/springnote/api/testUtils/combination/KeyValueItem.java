package com.springnote.api.testUtils.combination;

public record KeyValueItem<T>(T key, String value) {
    @Override
    public String toString() {
        return key + "=" + value;
    }
}
