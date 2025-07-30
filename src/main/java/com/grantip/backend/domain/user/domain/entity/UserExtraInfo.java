package com.grantip.backend.domain.user.domain.entity;

import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserExtraInfo extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1) user_id 외래키 맵핑
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 2) 자격 코드 컬렉션 / 어노테이션들
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "user_extra_qualifications",
            joinColumns = @JoinColumn(name = "user_extra_info_id")
    )
    @Column(name = "qualification_code")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<QualificationCode> qualificationCodes = new HashSet<>();

    // 3) 기타 필드
    @Column(name = "high_school_grade")
    private float highSchoolGrade;

    // 수능 평균 등급
    @Column(name = "sat_average_grade")
    private Double satAverageGrade;

    // 재학 중인 대학의 성적 기준 (4.5 또는 4.3)
    @Column(name = "gpa_scale")
    private Double gpaScale;

    // 대학교 전체 평균 학점
    @Column(name = "overall_gpa")
    private Double overallGpa;

    // 직전 학기 이수 학점
    @Column(name = "previous_semester_credits")
    private Integer previousSemesterCredits;

    // 직전 학기 평균 학점
    @Column(name = "previous_semester_gpa")
    private Double previousSemesterGpa;

    // 2학기 전 이수 학점
    @Column(name = "two_semesters_ago_credits")
    private Integer twoSemestersAgoCredits;

    // 2학기 전 평균 학점
    @Column(name = "two_semesters_ago_gpa")
    private Double twoSemestersAgoGpa;

    @Column(name = "scholarship_support_interval")
    private int scholarshipSupportInterval;

    @Column(name = "median_income_ratio")
    private int medianIncomeRatio;

    @Column(name = "income_percentile_band")
    private int incomePercentileBand;
}


