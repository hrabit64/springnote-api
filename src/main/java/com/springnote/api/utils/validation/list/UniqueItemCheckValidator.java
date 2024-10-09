package com.springnote.api.utils.validation.list;

import com.springnote.api.dto.general.common.PostTagId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class UniqueItemCheckValidator implements ConstraintValidator<UniqueItemCheck, List<PostTagId>> {
    @Override
    public boolean isValid(List<PostTagId> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty() || value.size() == 1) {
            return true;
        }

        return value.stream().map(PostTagId::getId).distinct().count() == value.size();
    }
}

