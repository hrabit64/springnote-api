package com.springnote.api.domain;

import com.springnote.api.utils.validation.query.comm.QueryKeyCommOption;
import com.springnote.api.utils.validation.query.ind.QueryKeyIndOption;

import java.util.List;

public interface QueryKeys {
    String getQueryString();

    Class<?> getType();

    // 각 값에 개별적으로 적용되는 옵션
    List<QueryKeyIndOption> getIndOptions();

    // 전체 값에 적용되는 옵션
    List<QueryKeyCommOption> getCommOptions();


    // 반드시 같이 있어야하는 필드
    // ex ) [ ["name", "age"] ] 이면 name 필드가 있을때는 age 필드도 있어야 함. 반대로 동일하게 age 필드가 있을때는 name 필드도 있어야 함.
    // 물론 name 필드가 없을때는 age 필드가 없어도 됨. 반대도 마찬가지
    List<List<String>> getRequiredFields();
}
