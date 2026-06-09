package com.ll.projectLimC.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.parameters.P;

@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
}
