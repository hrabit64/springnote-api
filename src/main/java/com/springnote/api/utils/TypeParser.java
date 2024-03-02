package com.springnote.api.utils;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class TypeParser {
    
    public Optional<Long> parseLong(String target){
        try {   
            return Optional.of(Long.parseLong(target));
        } catch(NumberFormatException e){
            return Optional.empty();
        }
    }
}
