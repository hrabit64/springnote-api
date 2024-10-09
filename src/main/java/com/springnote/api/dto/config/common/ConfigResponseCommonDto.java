package com.springnote.api.dto.config.common;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.config.Config;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConfigResponseCommonDto {
    private String key;
    private String value;

    public ConfigResponseCommonDto(Config config) {
        this.key = config.getKey();
        this.value = config.getValue();
    }
}
