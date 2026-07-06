package com.ll.projectLimC.global.Execption;

import com.ll.projectLimC.global.Execption.CommonResponse;
import com.ll.projectLimC.global.Execption.ErrorCode;
import com.ll.projectLimC.global.Execption.GlobalCustomException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 존재하지 않는 요청 또는 지원하지 않는 HTTP 메서드 예외 처리
    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public CommonResponse<?> handleNoPageFoundException(Exception e) {
        log.error("GlobalExceptionHandler catch Routing/Method Exception", e);
        return CommonResponse.fail(ErrorCode.NOT_FOUND_END_POINT);
    }

    // 2. 비즈니스 런타임 예외 처리 (내가 직접 throw 한 에러들)
    @ExceptionHandler(GlobalCustomException.class)
    public CommonResponse<?> handleGlobalCustomException(GlobalCustomException e) {
        log.warn("handleCustomException() -> ErrorCode: {}, Message: {}", e.getErrorCode().name(), e.getMessage());
        return CommonResponse.fail(e.getErrorCode());
    }

    // 3. 컨트롤러 @Valid 검증 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String defaultMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("Validation Failed : {}", defaultMessage);
        return CommonResponse.fail(ErrorCode.INVALID_FILTER_PARAMETER);
    }

    // 4. 시스템 최상위 기본 예외 (예상치 못한 500 서버 에러 처리)
    @ExceptionHandler(Exception.class)
    public CommonResponse<?> handleException(Exception e) {
        log.error("handleException() Unexpected Server Error 발생!", e);
        return CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}