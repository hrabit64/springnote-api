package com.springnote.api;

import com.github.database.rider.core.api.dataset.DataSet;
import com.springnote.api.testUtils.template.IGTestTemplate;
import org.junit.jupiter.api.Test;

class ApiApplicationTests extends IGTestTemplate {

    @DataSet(value = "datasets/ig/config/base-config.yaml")
    @Test
    void contextLoads() {
    }

}
