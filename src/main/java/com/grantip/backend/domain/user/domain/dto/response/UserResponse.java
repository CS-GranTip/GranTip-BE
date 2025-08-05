package com.grantip.backend.domain.user.domain.dto.response;

import com.grantip.backend.domain.region.domain.entity.Region;
import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.domain.scholarship.domain.entity.UniversityCategory;
import com.grantip.backend.domain.user.domain.constant.Gender;
import com.grantip.backend.domain.user.domain.constant.UnivYear;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    // --- User 필드 ---
    private String phone;

    //private UniversityCategory universityCategory;

    private String currentSchool;

    private String highSchool;

    @Schema(example = "SEVENTH_SEMESTER")
    private UnivYear universityYear;

    private Gender gender;

    private RegionDto address;

    private RegionDto residentAddress;

    // --- UserExtraInfo 필드 ---
    @Builder.Default
    private Set<QualificationCode> qualificationCodes = new HashSet<>();
    // qualificationCodes 는 기본값으로 빈 HashSet 을 주입해서 null 검사를 피합니다.

    private Double highSchoolGrade;

    private Double satAverageGrade;

    private Double gpaScale;

    private Double overallGpa;

    private Integer previousSemesterCredits;

    private Double previousSemesterGpa;

    private Integer twoSemestersAgoCredits;

    private Double twoSemestersAgoGpa;

    private Integer scholarshipSupportInterval;

    private Integer medianIncomeRatio;

    private Integer incomePercentileBand;
}


