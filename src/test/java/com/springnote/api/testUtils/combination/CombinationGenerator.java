package com.springnote.api.testUtils.combination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CombinationGenerator {

    public static Set<List<?>> generate(Set<?> elements) {
        Set<List<?>> result = new HashSet<>();
        List<Object> list = new ArrayList<>(elements);
        for (int i = 1; i <= list.size(); i++) {
            generateCombinations(list, i, 0, new ArrayList<>(), result);
        }
        return result;
    }

    private static void generateCombinations(List<Object> list, int combinationSize, int start, List<Object> current, Set<List<?>> result) {
        if (current.size() == combinationSize) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < list.size(); i++) {
            current.add(list.get(i));
            generateCombinations(list, combinationSize, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

}
