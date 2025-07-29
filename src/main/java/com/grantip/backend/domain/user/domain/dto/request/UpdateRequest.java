package com.grantip.backend.domain.user.domain.dto.request;

import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.domain.user.domain.constant.Gender;
import com.grantip.backend.domain.user.domain.constant.UnivYear;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
    // --- User 필드 ---
    @NotBlank
    private String phone;

    @NotNull
    private Long universityCategoryId;

    @NotBlank
    private String currentSchool;

    @NotBlank
    private String highSchool;

    @NotNull
    private UnivYear universityYear;

    @NotNull
    private Gender gender;

    @NotBlank
    private String address;

    @NotBlank
    private String residentAddress;

    // --- UserExtraInfo 필드 ---
    @Builder.Default
    private Set<QualificationCode> qualificationCodes = new HashSet<>();
    // qualificationCodes 는 기본값으로 빈 HashSet 을 주입해서 null 검사를 피합니다.

    private float highSchoolGrade;

    private String satScores;

    private float universityGrade;

    private int scholarshipSupportInterval;

    private int medianIncomeRatio;

    private int incomePercentileBand;
}

