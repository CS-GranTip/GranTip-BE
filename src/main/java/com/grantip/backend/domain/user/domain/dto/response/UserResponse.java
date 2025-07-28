package com.grantip.backend.domain.user.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    String email;
    String username;
}
