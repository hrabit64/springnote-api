package com.springnote.api.utils.type;

import com.springnote.api.domain.post.PostKeywordSearchMode;


public class TypeValidator {

    /**
     *
     */
    public static boolean isValid(String value, Class<?> type) {
        try {
            switch (type.getSimpleName()) {
                case "Integer" -> Integer.parseInt(value);
                case "Long" -> Long.parseLong(value);
                case "Double" -> Double.parseDouble(value);
                case "Float" -> Float.parseFloat(value);
                case "Boolean" -> Boolean.parseBoolean(value);
                case "Character" -> {
                    if (value.length() != 1) {
                        throw new IllegalArgumentException();
                    }
                }
                case "Byte" -> Byte.parseByte(value);
                case "Short" -> Short.parseShort(value);

                case "String" -> {
                    return true;
                }
                case "PostKeywordSearchMode" -> {
                    if (value == null || value.isBlank()) {
                        return false;
                    }
                    if (value.equalsIgnoreCase("none")) {
                        return false;
                    }
                    PostKeywordSearchMode.valueOf(value);
                }

                default -> {
                    // 지원하지 않는 타입
                    return false;
                }
            }
            return true; // 파싱 가능
        } catch (IllegalArgumentException e) {
            return false; // 파싱 불가능
        }
    }
}
