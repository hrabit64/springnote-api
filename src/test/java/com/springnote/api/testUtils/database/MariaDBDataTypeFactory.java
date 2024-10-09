package com.springnote.api.testUtils.database;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

import java.util.Collections;
import java.util.List;

public class MariaDBDataTypeFactory extends DefaultDataTypeFactory {

    @Override
    public List<String> getValidDbProducts() {
        return Collections.singletonList("MariaDB");
    }

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException, DataTypeException {
        // Add custom data type mappings if necessary
        return super.createDataType(sqlType, sqlTypeName);
    }
}
