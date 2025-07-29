package com.grantip.backend.domain.user.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "발급된 AccessToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "발급된 RefreshToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}

/////////////////////////////////////////////////////////////////
// 이거 필요없을듯 ?

