package com.springnote.api.utils.validation.query.ind;

public class StringLength implements QueryKeyIndOption {


    private final int min;
    private final int max;

    @Override
    public Boolean isValid(String value) {

        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            var size = value.length();
            return (size >= min) && (size <= max);
        } catch (NumberFormatException e) {
            return false;
        }

    }

    @Override
    public String getMessage() {

        return "%d ~ %d 사이의 길이만 입력 가능합니다.".formatted(min, max);
    }

    public StringLength(int min, int max) {
        this.min = min;
        this.max = max;
    }
}
