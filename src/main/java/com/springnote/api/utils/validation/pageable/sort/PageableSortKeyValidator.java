package com.springnote.api.utils.validation.pageable.sort;

import com.springnote.api.domain.SortKeys;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.springnote.api.utils.list.ListHelper.ignoreCaseContains;

public class PageableSortKeyValidator implements ConstraintValidator<PageableSortKeyCheck, Pageable> {

    private List<String> sortKeys;

    @Override
    public void initialize(PageableSortKeyCheck constraintAnnotation) {
        sortKeys = Stream.of(constraintAnnotation.sortKey().getEnumConstants())
                .map(SortKeys::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(Pageable value, ConstraintValidatorContext context) {
        if (value.getSort().isEmpty()) {
            return true;
        }
        return value.getSort().stream().allMatch(sort -> ignoreCaseContains(sortKeys, sort.getProperty()));
    }
}
