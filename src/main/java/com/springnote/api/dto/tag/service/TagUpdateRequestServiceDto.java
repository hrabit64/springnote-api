package com.springnote.api.dto.tag.service;

import com.springnote.api.domain.tag.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagUpdateRequestServiceDto {
    
    private Long id;
    private String name;

    public Tag toEntity(){
        return Tag.builder()
                .name(name)
                .build();
    }
}
