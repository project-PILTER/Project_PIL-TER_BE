package com.ll.projectLimC.global.Execption;

import lombok.Getter;
import com.ll.projectLimC.global.Execption.ErrorCode;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Getter
public class GlobalCustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public GlobalCustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public GlobalCustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
