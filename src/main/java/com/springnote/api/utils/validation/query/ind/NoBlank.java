package com.springnote.api.utils.validation.query.ind;

public class NoBlank implements QueryKeyIndOption {

    @Override
    public Boolean isValid(String value) {

        if (value == null || value.isBlank()) {
            return true;
        }

        for (var c : value.toCharArray()) {
            if (c == ' ') {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getMessage() {

        return "빈 값은 허용되지 않습니다.";
    }
}
