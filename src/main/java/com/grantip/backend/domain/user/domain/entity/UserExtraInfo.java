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
    private float high_school_grade;

    // SAT 점수들을 JSON 이나 CSV 형태로 문자열에 통째로 담아둘 때
    @Lob
    @Column(name = "sat_scores", columnDefinition = "TEXT")
    private String sat_scores;

    @Column(name = "university_grade")
    private float university_grade;

    @Column(name = "scholarship_support_interval")
    private int scholarship_support_interval;

    @Column(name = "median_income_ratio")
    private int median_income_ratio;

    @Column(name = "income_percentile_band")
    private int income_percentile_band;
}


