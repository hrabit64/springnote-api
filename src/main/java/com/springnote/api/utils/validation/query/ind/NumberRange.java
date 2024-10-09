package com.springnote.api.utils.validation.query.ind;

import com.springnote.api.utils.type.DBTypeSize;
import org.jetbrains.annotations.NotNull;

/**
 * 쿼리파라미터의 값이 숫자 범위에 속하는지 검증합니다.
 */
public class NumberRange implements QueryKeyIndOption {


    private final @NotNull Long min;
    private final @NotNull Long max;

    @Override
    public Boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            var number = Long.parseLong(value);
            return (number >= min) && (number <= max);
        } catch (NumberFormatException e) {
            return false;
        }

    }

    @Override
    public String getMessage() {
        return "%d ~ %d 사이의 숫자만 입력 가능합니다.".formatted(min, max);
    }

    public NumberRange(@NotNull Long min, @NotNull Long max) {
        this.min = min;
        this.max = max;
    }

    public static NumberRange of(Class<?> type) {
        if (type == Long.class) {
            return new NumberRange(1L, DBTypeSize.INT);
        } else if (type == Integer.class) {
            return new NumberRange(1L, DBTypeSize.INT);
        } else {
            throw new IllegalArgumentException("지원하지 않는 타입입니다.");
        }
    }
}
