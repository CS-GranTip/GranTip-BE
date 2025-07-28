package com.grantip.backend.domain.scholarship.domain.entity;

import com.grantip.backend.domain.scholarship.domain.constant.BaseSemester;
import com.grantip.backend.domain.scholarship.domain.constant.GradeCriterionType;
import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.domain.scholarship.domain.constant.ThresholdDirection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@Table(name = "grade_criterion")
public class GradeCriterion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "BIGINT")
  private Long id;

  // Scholarship과의 다대일 관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scholarship_id", nullable = false)
  private Scholarship scholarship;

  // 대상 그룹 (예: '신입생', '재학생')
  @Column(length = 100)
  private String group;

  // 기준 종류
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private GradeCriterionType type;

  // GPA(4.5점 만점)나 백분위 점수 등
  private Double score5;

  // GPA(4.3점 만점)
  private Double score3;

  // 이수학점 기준
  private Integer credits;

  // 석차/등급
  private Double rank;

  // 단위 (등급, %, 점수 등)
  @Column(length = 50)
  private String unit;

  // ETC 키워드 기준
  @Column(length = 255)
  private String keyword;

  // 기준 방향 (이상, 이하 등)
  @Enumerated(EnumType.STRING)
  @Column(length = 50)
  private ThresholdDirection direction;

  // 기준 학기
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private BaseSemester semester;

  // 원본 텍스트
  @Column(length = 500)
  private String description;

  // 필수 자격 조건 목록 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<QualificationCode> requiredQualifications;

  // 우대 조건 목록 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<QualificationCode> preferenceQualifications;
}