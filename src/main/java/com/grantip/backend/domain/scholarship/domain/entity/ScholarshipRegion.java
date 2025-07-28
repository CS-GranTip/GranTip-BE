package com.grantip.backend.domain.scholarship.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "scholarship_region")
public class ScholarshipRegion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 연관된 장학금
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "scholarship_id", nullable = false)
  private Scholarship scholarship;

  // 연관된 지역
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "region_id", nullable = false)
  private Region region;
}
