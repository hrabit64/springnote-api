package com.springnote.api.utils.validation.query.comm;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

/**
 * 쿼리키의 값의 개수를 제한하는 옵션
 * min, max 중 하나는 필수로 입력해야 함.
 * 둘 다 입력하면 min <= value.size() <= max
 * 둘 중 하나만 입력하면 min <= value.size() or value.size() <= max
 * 둘 다 같은 값이면 value.size() == min(=max)
 */
@RequiredArgsConstructor
public class LimitFieldCount implements QueryKeyCommOption {

    private final Integer min;
    private final Integer max;

    @Override
    public Boolean isValid(List<String> value) {

        if (max == null && min == null) {
            throw new IllegalArgumentException("min, max 중 하나는 필수로 입력해야 합니다.");
        }


        if (max == null) {
            return value.size() >= min;
        } else if (min == null) {
            return value.size() <= max;
        } else if (Objects.equals(min, max)) {
            return value.size() == min;
        } else {
            return value.size() >= min && value.size() <= max;
        }

    }

    @Override
    public String getMessage() {
        if (max == null && min == null) {
            throw new IllegalArgumentException("min, max 중 하나는 필수로 입력해야 합니다.");
        }

        if (max == null) {
            return "%d 개 이상의 값만 입력 가능합니다.".formatted(min);
        } else if (min == null) {
            return "%d 개 이하의 값만 입력 가능합니다.".formatted(max);
        } else if (Objects.equals(min, max)) {
            return "%d 개의 값만 입력 가능합니다.".formatted(min);
        } else {
            return "%d ~ %d 개의 값만 입력 가능합니다.".formatted(min, max);
        }

    }
}
