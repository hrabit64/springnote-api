// package com.springnote.api.config;

// import org.hibernate.boot.model.FunctionContributions;
// import org.hibernate.dialect.MariaDBDialect;
// import org.hibernate.type.StandardBasicTypes;

// public class CustomMariadbDialect extends MariaDBDialect {

//     @Override
//     public void initializeFunctionRegistry(FunctionContributions functionContributions) {
//         super.initializeFunctionRegistry(functionContributions);

//         var basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();

//         functionContributions.getFunctionRegistry().registerPattern(
//                 "match",
//                 "match (?1) against (?2)",
//                 basicTypeRegistry.resolve(StandardBasicTypes.BOOLEAN));
    
//     }

// }
