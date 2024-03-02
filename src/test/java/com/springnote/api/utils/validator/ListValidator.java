package com.springnote.api.utils.validator;

import java.util.List;

public class ListValidator {

    public static void isSameList(List<?> list1, List<?> list2) {
        
        if (list1.size() != list2.size()) {
            throw new AssertionError("List size is not same. list1 is (" + list1.size() + ") but list2 is (" + list2.size()+")");
        }

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                throw new AssertionError("List is not same. list1 has (" + list1.get(i) + ") but list2 has (" + list2.get(i)+")");
            }
        }

    }
    
}
