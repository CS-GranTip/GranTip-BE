package com.grantip.backend.domain.scholarship.domain.entity;

import com.grantip.backend.domain.scholarship.domain.constant.QualificationCode;
import com.grantip.backend.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "general_criterion")
public class GeneralCriterion extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Scholarship과의 다대일 관계
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scholarship_id", nullable = false)
  private Scholarship scholarship;

  // 장학금 지원을 위한 필수 자격 조건 목록 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<QualificationCode> requiredQualifications;

  // 우대 조건 목록 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<QualificationCode> preferenceQualifications;
}