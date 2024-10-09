package com.springnote.api.utils.validation.list;

import jakarta.validation.Payload;

public @interface UniqueItemCheck {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "리스트의 아이템이 중복되었습니다!";
}
