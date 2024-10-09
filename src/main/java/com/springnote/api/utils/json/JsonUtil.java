package com.springnote.api.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {
//    private final Gson gson;
//
//    public JsonUtil() {
//        var gsonBuilder = new GsonBuilder();
//
//        //localdate
//        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
//        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
//
//        //localdatetime
//        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
//        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
//        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
//        gson = gsonBuilder.setPrettyPrinting().create();
//    }
//
//    public String toJson(Object object) {
//        return gson.toJson(object);
//    }

    private final ObjectMapper objectMapper;

    public JsonUtil() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
