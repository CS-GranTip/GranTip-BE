package com.grantip.backend.domain.scholarship.domain.entity;

import com.grantip.backend.domain.scholarship.domain.constant.AidType;
import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.global.util.BaseEntity;
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
@Table(name = "income_criterion")
public class IncomeCriterion extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Scholarship과의 다대일 관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scholarship_id", nullable = false)
  private Scholarship scholarship;

  // 규칙 적용 우선순위 (낮을수록 높음)
  private Integer priority;

  // 규칙에 대한 설명
  @Column(length = 500)
  private String description;

  // 규칙이 적용되는 지원금 종류
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private AidType aidType;

  // 필수 자격 조건 목록 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<QualificationCode> requiredQualifications;

  // 우대 조건 목록 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<QualificationCode> preferenceQualifications;

  // 소득/재산 기준 면제 여부
  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean ignoreIncomeAndAssets;

  // 한국장학재단 학자금 지원구간 (n구간 이하)
  private Integer scholarshipSupportInterval;

  // 소득분위 (n분위 이내)
  private Integer incomePercentileBand;

  // 기준 중위소득 비율 (n% 이하)
  private Integer medianIncomeRatio;
}