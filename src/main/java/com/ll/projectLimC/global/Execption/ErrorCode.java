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
    AUTHENTICATION_FAILED(4002, HttpStatus.UNAUTHORIZED, "인증 확인에 실패하였습니다."),
    SIGN_IN_PASSWORD_NOT_MATCH(4003, HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_THE_USER(4004, HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    NOT_FOUND_THE_COMMENT(4005, HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    UNAUTHORIED_THE_COMMENT(4006, HttpStatus.UNAUTHORIZED, "해당 댓글에 관한 권한이 없습니다."),
    NOT_FOUND_THE_ARTICLE(4007, HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    NOT_FOUND_THE_ARTICLE_ID(4008, HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_THE_ARTICLE(4009, HttpStatus.UNAUTHORIZED, "해당 게시글에 대한 권한이 없습니다."),
    NOT_FOUND_THE_HEALTHJOURNAL(4010, HttpStatus.NOT_FOUND, "존재하지 않는 건강일지입니다."),
    UNAUTHORIZED_THE_HEALTHJOURNAL(4011, HttpStatus.UNAUTHORIZED, "해당 건강일지에 대한 권한이 없습니다."),
    UNEXPECTED_TOKEN(4012, HttpStatus.UNAUTHORIZED, "만료된 유저입니다."),
    UNEXPECTED_VALIDATION_TOKEN(4013, HttpStatus.BAD_REQUEST, "토큰 유효성 검사에 실패하였습니다."),
    NOT_FOUND_THE_EMAIL_TO_SOCIAL(4014, HttpStatus.NOT_FOUND, "소셜 로그인으로부터 이메일 정보를 가져올 수 없습니다."),
    NOT_AUTHORIZED_THE_DRAFT_ARTICLE(4015, HttpStatus.UNAUTHORIZED, "해당 임시저장 글이 존재하지 않거나 권한이 없습니다."),
    NOT_FOUND_THE_DRAFT_ARTICLE(4016, HttpStatus.NOT_FOUND, "해당 임시저장 글을 찾을 수 없습니다."),
    UNAUTHORIZED_USER(4017, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다(401)"),
    CANNOT_NEST_DEEP_COMMENT(4018, HttpStatus.NOT_FOUND, "더 이상의 대댓글을 작성할 수 없습니다."),
    NO_EXIST_THAT_MEDICINE(4019, HttpStatus.NOT_FOUND, "해당 약품이 존재하지 않습니다."),
    // 5000번대
    INTERNAL_SERVER_ERROR(5000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
