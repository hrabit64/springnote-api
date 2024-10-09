package com.springnote.api.utils.validation.query;

import com.springnote.api.domain.QueryKeys;
import com.springnote.api.utils.type.TypeValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.springnote.api.utils.list.ListHelper.*;

public class QueryParamValidator implements ConstraintValidator<QueryParamCheck, MultiValueMap<String, String>> {

    private List<QueryKeys> queryKeys;
    private List<String> ignoreKeys = List.of("page", "size", "sort");

    @Override
    public void initialize(QueryParamCheck constraintAnnotation) {
        queryKeys = Stream.of(constraintAnnotation.queryKey().getEnumConstants())
                .collect(Collectors.toList());

    }

    @Override
    public boolean isValid(MultiValueMap<String, String> queryParams, ConstraintValidatorContext context) {
        var keys = queryParams.keySet();

        if (keys.isEmpty()) {
            return true;
        }

        var requiredFields = queryKeys.get(0).getRequiredFields();

        if (!requiredFields.isEmpty() && !checkRequiredFields(keys.stream().toList(), requiredFields)) {
            return false;
        }

        try {
            return checkQueryKeys(queryParams, keys.stream().toList());
        } catch (IllegalArgumentException e) {
            return false;
        }

    }

    /**
     * 쿼리 파라미터의 키와 값이 유효한지 검사합니다.
     *
     * @param queryParams 쿼리 파라미터
     * @param keys        쿼리 파라미터의 키
     * @return 유효하면 true, 그렇지 않으면 false
     */
    private boolean checkQueryKeys(MultiValueMap<String, String> queryParams, List<String> keys) {

        for (var key : keys) {
            if (ignoreCaseContains(ignoreKeys, key)) {
                continue;
            }
            var queryKey = queryKeys.stream()
                    .filter(queryKeys -> queryKeys.getQueryString().equalsIgnoreCase(key))
                    .findFirst()
                    .orElse(null);

            if (queryKey == null) {
                return false;
            }
            var values = queryParams.get(key);

            // 타입 체크


            if (!isValidCommOption(queryKey, values)) {
                return false;
            }

            values.forEach(value -> {
                if (!isValidIndOption(value, queryKey)) {
                    throw new IllegalArgumentException(queryKey.getQueryString() + "의 값이 유효하지 않습니다.");
                }
            });
        }

        return true;
    }

    private boolean isValidCommOption(QueryKeys queryKey, List<String> values) {
        if (queryKey.getCommOptions() == null) return true;

        return queryKey.getCommOptions().stream().allMatch(option -> option.isValid(values));
    }

    private boolean isValidIndOption(String value, QueryKeys queryKey) {
        var type = queryKey.getType();
        var options = queryKey.getIndOptions();

        if (!TypeValidator.isValid(value, type)) {
            return false;
        }

        return options == null || options.stream().allMatch(option -> option.isValid(value));

    }

    private boolean checkRequiredFields(List<String> keys, List<List<String>> requiredFields) {
        for (var fields : requiredFields) {
            if (ignoreCaseAnyContains(keys, fields)) {
                if (!ignoreCaseContainsAll(keys, fields)) {
                    return false;
                }
            }
        }
        return true;
    }


}
