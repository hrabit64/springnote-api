package com.springnote.api.utils.validation.query.comm;

import java.util.List;

public class UniqueItems implements QueryKeyCommOption {


    @Override
    public Boolean isValid(List<String> value) {
        return value.stream().distinct().count() == value.size();
    }

    @Override
    public String getMessage() {
        return "중복된 값이 있습니다.";
    }
}
