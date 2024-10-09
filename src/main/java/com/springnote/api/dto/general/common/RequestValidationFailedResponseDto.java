package com.springnote.api.dto.general.common;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestValidationFailedResponseDto {

    private LocalDateTime timestamp;
    private HttpStatus status;
    private List<String> errors;
    private String code;
    private String message;
    private String path;
}
