package com.grantip.backend.domain.scholarship.domain.constant;

public enum QualificationCode {
  // --- 사회/경제적 상태 ---
  LOW_INCOME,         // 저소득층 (기초/차상위 포함)

  // --- 가구/개인 특성 ---
  MULTI_CHILD,        // 다자녀 가구
  SINGLE_PARENT,      // 한부모가족
  BOY_GIRL_HEADED,    // 소년소녀가장
  DISABLED,           // 장애인 (본인 또는 가구원)
  MULTICULTURAL,      // 다문화가정
  NATIONAL_MERIT,     // 국가유공자 / 보훈대상자
  NORTH_KOREAN_SETTLER, // 북한이탈주민

  // --- 학적 상태 ---
  FRESHMAN,           // 신입생 / 입학생
  ENROLLED            // 재학생
}
