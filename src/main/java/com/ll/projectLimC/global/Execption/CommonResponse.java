package com.ll.projectLimC.global.Execption;

import org.springframework.http.HttpStatus;

public record CommonResponse<T>(
        Boolean isSuccess,
        Integer code,
        String message,
        T result,
        HttpStatus httpStatus
) {
    public static <T> CommonResponse<T> success(T result) {
        return new CommonResponse<>(
                true,
                200,                // 성공 코드 (프로젝트 규칙에 맞춰 변경 가능)
                "요청에 성공하였습니다.",  // 성공 메시지
                result,
                HttpStatus.OK
        );
    }

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
