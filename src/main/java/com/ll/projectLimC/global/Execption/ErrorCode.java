package com.ll.projectLimC.global.Execption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 4000번대
    INVALID_FILTER_PARAMETER(4000, HttpStatus.BAD_REQUEST, "필터링 파라미터가 유효하지 않습니다."),
    NOT_FOUND_END_POINT(4001, HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    AUTHENTICATION_FAILED(4002, HttpStatus.BAD_REQUEST, "인증 확인에 실패하였습니다."),

    // 5000번대
    INTERNAL_SERVER_ERROR(5000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
