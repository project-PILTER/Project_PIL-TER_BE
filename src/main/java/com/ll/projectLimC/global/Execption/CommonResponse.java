package com.ll.projectLimC.global.Execption;

import org.springframework.http.HttpStatus;

public record CommonResponse<T>(
        Boolean isSuccess,
        Integer code,
        String message,
        T result,
        HttpStatus httpStatus
) {
    // 실패 응답을 만드는 정적 팩토리 메서드
    public static CommonResponse<?> fail(ErrorCode errorCode) {
        return new CommonResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                errorCode.getHttpStatus()
        );
    }
}
