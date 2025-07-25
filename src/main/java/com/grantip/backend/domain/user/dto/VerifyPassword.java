package com.grantip.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPassword {
    @Schema(description = "새 비밀번호", example = "Password1")
    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    String password1;

    @Schema(description = "새 비밀번호 확인", example = "Password2")
    @NotBlank(message = "새 비밀번호를 다시 한 번 입력해 주세요.")
    String password2;
}
