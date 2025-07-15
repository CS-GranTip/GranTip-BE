package com.grantip.backend.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
/*
JSON 매핑 어노테이션 (선택)
프론트가 { "email": "…" } 형태로 보낸다면 @JsonProperty("email") 은 필요 없지만,
 혹시 서버에서 다른 이름을 기대하는 경우 매핑을 조정하세요.
 */