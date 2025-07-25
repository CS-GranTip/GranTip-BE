package com.grantip.backend.domain.user.dto;

import com.grantip.backend.domain.user.entity.Gender;
import com.grantip.backend.domain.user.entity.QualificationCode;
import com.grantip.backend.domain.user.entity.UnivYear;
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

    @NotBlank
    private String current_school;

    @NotBlank
    private String high_school;

    @NotNull
    private UnivYear university_year;

    @NotNull
    private Gender gender;

    @NotBlank
    private String address;

    @NotBlank
    private String resident_address;

    // --- UserExtraInfo 필드 ---
    @Builder.Default
    private Set<QualificationCode> qualificationCodes = new HashSet<>();
    // qualificationCodes 는 기본값으로 빈 HashSet 을 주입해서 null 검사를 피합니다.

    private float high_school_grade;

    private String sat_scores;

    private float university_grade;

    private int scholarship_support_interval;

    private int median_income_ratio;

    private int income_percentile_band;
}

