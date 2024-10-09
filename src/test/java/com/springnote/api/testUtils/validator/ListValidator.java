package com.springnote.api.testUtils.validator;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ListValidator {

    public static boolean isSameList(List<?> list1, List<?> list2) {

        if (list1.size() != list2.size()) {

            log.error("List size is not same. list1 size is ({}) but list2 size is ({})", list1.size(), list2.size());
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                log.error("List element is not same. list1[{}] element is ({}) but list2[{}] element is ({})", i, list1.get(i), i, list2.get(i));
                return false;
            }
        }

        return true;

    }

}
