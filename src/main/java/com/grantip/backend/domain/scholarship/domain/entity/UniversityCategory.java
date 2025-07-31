package com.grantip.backend.domain.scholarship.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "university_category")
public class UniversityCategory {

  // 고유 ID (PK)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 카테고리 이름
  @Column(nullable = false, unique = true, length = 255)
  private String name;

  // 이 카테고리에 속하는 장학금 목록 (다대다 관계)
  @ManyToMany(mappedBy = "universityCategories")
  private Set<Scholarship> scholarships = new HashSet<>();
}
