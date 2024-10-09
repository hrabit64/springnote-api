package com.springnote.api.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;


public class MariadbFunctionContributor implements FunctionContributor {
    //    public void contributeFunctions(FunctionContributions functionContributions) {
//        functionContributions.getFunctionRegistry().registerPattern("match", "MATCH (?1) AGAINST (?2 IN BOOLEAN MODE)"
//        , functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.DOUBLE));
//    }
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry().registerPattern("match", "MATCH (?1) AGAINST (?2)"
                , functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.DOUBLE));
    }
}
