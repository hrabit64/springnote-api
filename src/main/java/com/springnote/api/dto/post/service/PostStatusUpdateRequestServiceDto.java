package com.springnote.api.dto.post.service;

import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostStatusUpdateRequestServiceDto {
    private Long id;
    private boolean isEnabled;
}
