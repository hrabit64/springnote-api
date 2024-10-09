package com.springnote.api.domain;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;

public interface QRepository {
    BooleanExpression createWhereExpression(MultiValueMap<String, String> whereOptions);

    OrderSpecifier<?>[] createOrderSpecifiers(Sort sort);

}
