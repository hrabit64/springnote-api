package com.springnote.api.utils.validation.query.comm;

import java.util.List;

public interface QueryKeyCommOption {
    Boolean isValid(List<String> value);

    String getMessage();
}
