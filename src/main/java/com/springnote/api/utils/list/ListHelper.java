package com.springnote.api.utils.list;

import java.util.List;

public class ListHelper {

    public static void isSameList(List<?> list1, List<?> list2) {

        if (list1.size() != list2.size()) {
            throw new AssertionError("List size is not same. list1 is (" + list1.size() + ") but list2 is (" + list2.size() + ")");
        }


        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                throw new AssertionError("List is not same. list1 has (" + list1.get(i) + ") but list2 has (" + list2.get(i) + ")");
            }
        }

    }

    /**
     * 대소문자를 무시하고 리스트에 값이 포함되어 있는지 확인합니다.
     *
     * @param list  리스트
     * @param value 값
     * @return 포함되어 있으면 true, 그렇지 않으면 false
     */
    public static boolean ignoreCaseContains(List<String> list, String value) {
        return list.stream().anyMatch(s -> s.equalsIgnoreCase(value));
    }

    /**
     * 대소문자를 무시하고 list에 values가 하나 이상 포함되어 있는지 확인합니다.
     *
     * @param list   리스트
     * @param values 값
     * @return 포함되어 있으면 true, 그렇지 않으면 false
     */
    public static boolean ignoreCaseAnyContains(List<String> list, List<String> values) {
        return values.stream().anyMatch(value -> ignoreCaseContains(list, value));
    }


    /**
     * 대소문자를 무시하고 list에 values가 모두 포함되어 있는지 확인합니다.
     *
     * @param list   리스트
     * @param values 값
     * @return 포함되어 있으면 true, 그렇지 않으면 false
     */
    public static boolean ignoreCaseContainsAll(List<String> list, List<String> values) {
        return values.stream().allMatch(value -> ignoreCaseContains(list, value));
    }

}
