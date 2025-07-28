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
@Table(name = "region")
public class Region {

  // 고유 ID (PK)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 상위 지역 (자기 참조)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Region parent;

  // 지역 이름
  @Column(name = "region_name", nullable = false, length = 255)
  private String regionName;

  // 지역 계층 레벨
  @Column(name = "region_level", nullable = false)
  private Integer regionLevel;
}