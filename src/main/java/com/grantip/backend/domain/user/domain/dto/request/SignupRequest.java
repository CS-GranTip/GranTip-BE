package com.grantip.backend.domain.user.domain.dto.request;


import com.grantip.backend.domain.user.domain.constant.Gender;
import com.grantip.backend.domain.user.domain.constant.Role;
import com.grantip.backend.domain.user.domain.constant.UnivYear;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일을 입력해주세요.")
    @Schema(example = "cisdid202@naver.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Schema(example = "password")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Schema(example = "이찬양")
    private String username;

    @Schema(example = "USER")
    private Role role;

    @NotBlank
    @Schema(example = "01012345678")
    private String phone;

    @NotNull
    @Schema(example = "1")
    private Long universityCategoryId;

    @NotBlank
    @Schema(example = "세종대학교")
    private String currentSchool;

    @NotBlank
    @Schema(example = "양천고등학교")
    private String highSchool;

    @Schema(example = "SEVENTH_SEMESTER")
    private UnivYear universityYear;

    @Schema(example = "MALE")
    private Gender gender;

    @NotNull
    private Long addressId;

    @NotNull
    private Long residentAddressId; //주민등록상 주소

    @Column(nullable = false)
    private boolean available = true;
}
