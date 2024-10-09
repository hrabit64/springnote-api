package com.springnote.api.utils.type;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TypeParser {

    public Optional<Long> parseLong(String target) {
        try {
            return Optional.of(Long.parseLong(target));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Integer> parseInt(String target) {
        try {
            return Optional.of(Integer.parseInt(target));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Double> parseDouble(String target) {
        try {
            return Optional.of(Double.parseDouble(target));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Float> parseFloat(String target) {
        try {
            return Optional.of(Float.parseFloat(target));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<Boolean> parseBoolean(String target) {

        return Optional.of(Boolean.parseBoolean(target));
    }

    public Optional<String> parseString(String target) {
        return Optional.of(target);
    }

    public Optional<Character> parseCharacter(String target) {
        return Optional.of(target.charAt(0));
    }

    public Optional<Short> parseShort(String target) {
        try {
            return Optional.of(Short.parseShort(target));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<?> parse(String target, Class<?> type) {
        return switch (type.getSimpleName()) {
            case "Long" -> parseLong(target);
            case "Integer" -> parseInt(target);
            case "Double" -> parseDouble(target);
            case "Float" -> parseFloat(target);
            case "Boolean" -> parseBoolean(target);
            case "String" -> parseString(target);
            case "Character" -> parseCharacter(target);
            case "Short" -> parseShort(target);
            default -> throw new IllegalArgumentException("Given cannot Pares Type : " + type.getSimpleName());
        };
    }
}
