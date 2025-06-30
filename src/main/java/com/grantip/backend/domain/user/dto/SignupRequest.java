package com.grantip.backend.domain.user.dto;


import com.grantip.backend.domain.user.entity.Gender;
import com.grantip.backend.domain.user.entity.UnivYear;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Schema(example = "cksdid202")
    private String loginId;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Schema(example = "cksdid202@naver.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Schema(example = "password")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Schema(example = "이찬양")
    private String username;

    @NotBlank(message = "역할은 필수 입력값입니다.")
    @Schema(example = "USER")
    private String role;

    @Schema(example = "01012345678")
    private String phone;

    @Schema(example = "세종대학교")
    private String university;

    @Schema(example = "양천고등학교")
    private String highschool;

    @NotBlank(message = "학년은 필수 입력값입니다.")
    @Schema(example = "4학년")
    private UnivYear univYear;

    @NotBlank(message = "성별은 필수 입력값입니다.")
    private Gender gender;

    private String address;

    private String resiAddress; //주민등록상 주소


}
