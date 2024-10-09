package com.springnote.api.utils.uuid;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidUtils {
    public String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
