package com.ll.projectLimC.global.Execption;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final String message;
    private final String code;
}
