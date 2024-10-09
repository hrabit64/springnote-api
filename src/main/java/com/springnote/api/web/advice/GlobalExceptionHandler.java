package com.springnote.api.web.advice;

import com.springnote.api.dto.general.common.ExceptionResponseDto;
import com.springnote.api.utils.exception.SpringNoteException;
import com.springnote.api.utils.exception.auth.AuthException;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.exception.validation.ValidationException;
import com.springnote.api.utils.formatter.LogFormatter;
import com.springnote.api.utils.time.TimeHelper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final TimeHelper timeHelper;

    @ExceptionHandler({BusinessException.class, AuthException.class, ValidationException.class})
    protected ResponseEntity<ExceptionResponseDto> handleSpringnoteExceptions(SpringNoteException exception,
                                                                              WebRequest request) {
        log.error(LogFormatter.createExceptionString(exception.getClass().getSimpleName(),
                "code - [ " + exception.getErrorCode().getTitle() + " ] - " + exception.getMessage()));
        log.debug(Arrays.toString(exception.getStackTrace()), exception);
        return ResponseEntity
                .status(exception.getErrorCode().getStatusCode())
                .body(
                        ExceptionResponseDto
                                .builder()
                                .title(exception.getErrorCode().getTitle())
                                .message(exception.getMessage())
                                .status(HttpStatus.valueOf(exception.getErrorCode()
                                        .getStatusCode()))
                                .timestamp(timeHelper.nowTime())
                                .path(request.getContextPath())
                                .build());
    }


    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ExceptionResponseDto> handleServerException(Exception exception, WebRequest request) {

        log.error(LogFormatter.createExceptionString(exception.getClass().getSimpleName(),
                exception.getMessage()));
        log.debug(Arrays.toString(exception.getStackTrace()), exception);
        return ResponseEntity.status(500).body(
                ExceptionResponseDto
                        .builder()
                        .title("UnknownError")
                        .message("서버에 알 수 없는 에러가 발생했습니다. 잠시후 다시 시도해주세요.")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            NoResourceFoundException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class,
            UnsupportedMediaTypeStatusException.class,
            BindException.class,
            TypeMismatchException.class,
            MissingServletRequestPartException.class,
            HttpClientErrorException.class,
            NoHandlerFoundException.class,
            MissingPathVariableException.class,
            HttpMediaTypeNotAcceptableException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingRequestHeaderException.class
    })
    protected ResponseEntity<ExceptionResponseDto> handleSpringBootDefaultExceptions(Exception exception, WebRequest request) {
        log.error(LogFormatter.createExceptionString(exception.getClass().getSimpleName(),
                exception.getMessage()));
        log.debug(Arrays.toString(exception.getStackTrace()), exception);

        return switch (exception.getClass().getSimpleName()) {
            case "HttpMessageNotReadableException" ->
                    handleHttpMessageNotReadableExceptionException((HttpMessageNotReadableException) exception, request);
            case "MethodArgumentNotValidException" ->
                    handleMethodArgumentNotValidException((MethodArgumentNotValidException) exception, request);
            case "NoResourceFoundException" ->
                    handleNoResourceFoundException((NoResourceFoundException) exception, request);
            case "MissingServletRequestParameterException" ->
                    handleMissingServletRequestParameterException((MissingServletRequestParameterException) exception, request);
            case "ConstraintViolationException" ->
                    handleConstraintViolationException((ConstraintViolationException) exception, request);
            case "HttpRequestMethodNotSupportedException" ->
                    handleHttpRequestMethodNotSupportedException((HttpRequestMethodNotSupportedException) exception, request);
            case "MethodArgumentTypeMismatchException" ->
                    handleMethodArgumentTypeMismatchException((MethodArgumentTypeMismatchException) exception, request);
            case "MissingRequestValueException" ->
                    handleMissingRequestValueException((MissingRequestValueException) exception, request);
            case "UnsupportedMediaTypeStatusException" ->
                    handleUnsupportedMediaTypeStatusException((UnsupportedMediaTypeStatusException) exception, request);
            case "BindException" -> handleBindException((BindException) exception, request);
            case "TypeMismatchException" -> handleTypeMismatchException((TypeMismatchException) exception, request);
            case "MissingServletRequestPartException" ->
                    handleMissingServletRequestPartException((MissingServletRequestPartException) exception, request);
            case "HttpClientErrorException" ->
                    handleHttpClientErrorException((HttpClientErrorException) exception, request);
            case "NoHandlerFoundException" ->
                    handleNoHandlerFoundException((NoHandlerFoundException) exception, request);
            case "MissingPathVariableException" ->
                    handleMissingPathVariableException((MissingPathVariableException) exception, request);
            case "HttpMediaTypeNotAcceptableException" ->
                    handleHttpMediaTypeNotAcceptableException((HttpMediaTypeNotAcceptableException) exception, request);
            case "HttpMediaTypeNotSupportedException" ->
                    handleHttpMediaTypeNotSupportedException((HttpMediaTypeNotSupportedException) exception, request);
            case "MissingRequestHeaderException" ->
                    handleMissingRequestHeaderException((MissingRequestHeaderException) exception, request);
            default -> handleServerException(exception, request);
        };
    }


    protected ResponseEntity<ExceptionResponseDto> handleHttpMessageNotReadableExceptionException(HttpMessageNotReadableException exception, WebRequest request) {
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("HttpMessageNotReadable")
                        .message("요청 데이터를 읽을 수 없습니다. 올바른 JSON 형식으로 요청해주세요.")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {

        var fieldBuilder = new StringBuilder();
        var errorMap = new LinkedMultiValueMap<String, String>();

        for (var fieldError : exception.getFieldErrors()) {
            errorMap.set(fieldError.getField(), fieldError.getDefaultMessage());
        }

        for (var field : errorMap.keySet()) {
            var messages = errorMap.get(field);

            var messageBuilder = new StringBuilder();

            if (messages != null) {
                messageBuilder.append(field).append(" : ");
                for (var message : messages) {
                    messageBuilder.append(message).append(",");
                }
                fieldBuilder.append(messageBuilder.toString()).append("\n");
            }
        }

        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("MethodArgumentNotValid")
                        .message(fieldBuilder.toString())
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleNoResourceFoundException(NoResourceFoundException exception, WebRequest request) {
        return ResponseEntity.status(404).body(
                ExceptionResponseDto
                        .builder()
                        .title("NoResourceFound")
                        .message("요청하신 리소스를 찾을 수 없습니다.")
                        .status(HttpStatus.NOT_FOUND)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception, WebRequest request) {
        var requiredParams = exception.getParameterName();
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("MissingServletRequestParameter")
                        .message("필수 요청 파라미터가 누락되었습니다. [" + requiredParams + "]")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleConstraintViolationException(ConstraintViolationException exception, WebRequest request) {

        var violationMessageBuilder = new StringBuilder();
        exception.getConstraintViolations().forEach(violation -> {
            violationMessageBuilder.append(violation.getMessage()).append("\n");
        });


        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("ConstraintViolation")
                        .message(violationMessageBuilder.toString())
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, WebRequest request) {
        var requiredMethods = exception.getMethod();
        return ResponseEntity.status(405).body(
                ExceptionResponseDto
                        .builder()
                        .title("HttpRequestMethodNotSupported")
                        .message("지원하지 않는 요청 메소드입니다. [" + requiredMethods + "]")
                        .status(HttpStatus.METHOD_NOT_ALLOWED)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, WebRequest request) {
        var field = exception.getName();
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("MethodArgumentTypeMismatch")
                        .message("요청 파라미터의 타입이 올바르지 않습니다. [" + field + "]")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }


    protected ResponseEntity<ExceptionResponseDto> handleMissingRequestValueException(MissingRequestValueException exception, WebRequest request) {
        var field = String.join(", ", Arrays.stream(Objects.requireNonNull(exception.getDetailMessageArguments()))
                .map(Object::toString)
                .toArray(String[]::new));
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("MissingRequestValue")
                        .message("필수 요청 값이 누락되었습니다. [" + field + "]")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }


    protected ResponseEntity<ExceptionResponseDto> handleUnsupportedMediaTypeStatusException(UnsupportedMediaTypeStatusException exception, WebRequest request) {
        return ResponseEntity.status(415).body(
                ExceptionResponseDto
                        .builder()
                        .title("UnsupportedMediaType")
                        .message("지원하지 않는 미디어 타입입니다.")
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }


    protected ResponseEntity<ExceptionResponseDto> handleBindException(BindException exception, WebRequest request) {
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("BindException")
                        .message("요청 데이터를 바인딩할 수 없습니다.")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleTypeMismatchException(TypeMismatchException exception, WebRequest request) {
        var field = exception.getPropertyName();
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("TypeMismatch")
                        .message("요청 파라미터의 타입이 올바르지 않습니다. [" + field + "]")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleMissingServletRequestPartException(MissingServletRequestPartException exception, WebRequest request) {
        var field = exception.getRequestPartName();
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("MissingServletRequestPart")
                        .message("필수 요청 파트가 누락되었습니다. [" + field + "]")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleHttpClientErrorException(HttpClientErrorException exception, WebRequest request) {
        return ResponseEntity.status(exception.getStatusCode()).body(
                ExceptionResponseDto
                        .builder()
                        .title("HttpClientError")
                        .message("클라이언트 요청이 실패했습니다.")
                        .status(HttpStatus.valueOf(exception.getStatusCode().value()))
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleNoHandlerFoundException(NoHandlerFoundException exception, WebRequest request) {
        return ResponseEntity.status(404).body(
                ExceptionResponseDto
                        .builder()
                        .title("NoHandlerFound")
                        .message("요청하신 핸들러를 찾을 수 없습니다.")
                        .status(HttpStatus.NOT_FOUND)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleMissingPathVariableException(MissingPathVariableException exception, WebRequest request) {
        var field = exception.getVariableName();
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("MissingPathVariable")
                        .message("필수 경로 변수가 누락되었습니다. [" + field + "]")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException exception, WebRequest request) {
        return ResponseEntity.status(406).body(
                ExceptionResponseDto
                        .builder()
                        .title("HttpMediaTypeNotAcceptable")
                        .message("요청한 미디어 타입이 지원되지 않습니다.")
                        .status(HttpStatus.NOT_ACCEPTABLE)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception, WebRequest request) {
        return ResponseEntity.status(415).body(
                ExceptionResponseDto
                        .builder()
                        .title("HttpMediaTypeNotSupported")
                        .message("지원하지 않는 미디어 타입입니다.")
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

    protected ResponseEntity<ExceptionResponseDto> handleMissingRequestHeaderException(MissingRequestHeaderException exception, WebRequest request) {
        var field = exception.getHeaderName();
        return ResponseEntity.status(400).body(
                ExceptionResponseDto
                        .builder()
                        .title("MissingRequestHeader")
                        .message("필수 요청 헤더가 누락되었습니다. [" + field + "]")
                        .status(HttpStatus.BAD_REQUEST)
                        .timestamp(timeHelper.nowTime())
                        .path(request.getContextPath())
                        .build());
    }

}