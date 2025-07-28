package com.grantip.backend.domain.scholarship.domain.entity;

import com.grantip.backend.domain.scholarship.domain.constant.ProductType;
import com.grantip.backend.domain.scholarship.domain.constant.ProviderType;
import com.grantip.backend.domain.scholarship.domain.constant.ScholarshipCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "scholarship")
public class Scholarship {

  // 시스템 내부 고유 ID (PK)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 운영기관이 제공한 원본 번호
  @Column(unique = true, nullable = false)
  private Integer originalId;

  @Column(length = 255, nullable = false)
  private String productName;

  @Column(length = 255, nullable = false)
  private String providerName;

  // 운영기관 구분
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private ProviderType providerType;

  // 상품 구분
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private ProductType productType;

  // 학자금 유형 구분
  @Enumerated(EnumType.STRING)
  @Column(length = 100)
  private ScholarshipCategory scholarshipCategory;

  private LocalDate applicationStartDate;

  private LocalDate applicationEndDate;

  @Column(length = 500)
  private String homepageUrl;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "scholarship_university_category",
      joinColumns = @JoinColumn(name = "scholarship_id"),
      inverseJoinColumns = @JoinColumn(name = "university_category_id")
  )
  private Set<UniversityCategory> universityCategories = new HashSet<>();

  // 학년 구분 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<String> gradeCategory;

  // 학과 구분 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private List<String> departmentCategory;

  // 총 선발 인원
  private Integer numOfRecipientsTotal;

  // 구분별 선발 인원 (JSON으로 저장)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private Map<String, Integer> recipientsByCategory;

  // 추천서 필요 여부
  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean isRecommendationRequired;

  // 중복 수혜 제한 여부
  @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private boolean isDuplicateSupportRestricted;

  @Column(length = 500)
  private String gradeCriteriaDetail;

  @Column(length = 500)
  private String incomeCriteriaDetail;

  @Column(length = 500)
  private String supportDetail;

  @Column(length = 500)
  private String specificQualificationDetail;

  @Column(length = 500)
  private String regionResidenceDetail;

  @Column(length = 500)
  private String selectionMethodDetail;

  @Column(length = 500)
  private String selectionPersonnelDetail;

  @Column(length = 500)
  private String qualificationRestrictionDetail;

  @Column(length = 500)
  private String recommendationNeededDetail;

  @Column(length = 500)
  private String requiredDocumentsDetail;

  @Column(length = 500)
  private String gradeCriteriaNotes;

  @Column(length = 500)
  private String incomeCriteriaNotes;

  @Column(length = 500)
  private String supportNotes;

  @Column(length = 500)
  private String specificQualificationNotes;

  @Column(length = 500)
  private String regionResidenceNotes;

  @Column(length = 500)
  private String selectionMethodNotes;

  @Column(length = 500)
  private String selectionPersonnelNotes;

  @Column(length = 500)
  private String qualificationRestrictionNotes;

  @Column(length = 500)
  private String recommendationNeededNotes;

  @Column(length = 500)
  private String requiredDocumentsNotes;
}
