package com.grantip.backend.domain.scholarship.entity;

import com.grantip.backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.net.URL;
import java.util.Date;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Scholarship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String agencyName;   //지원기업

    private String agencyType;

    @Enumerated(EnumType.STRING)
    private Category category;   //장학금 타입

    @Enumerated(EnumType.STRING)
    private SupportType supportType;    //지원타입

    @Enumerated(EnumType.STRING)
    private UniversityType universityType;  //대학타입

    private String academicDept;    //전공

    private String gradeCondition;

    private float gpaRequirement;  //필요 등급 (string > float)

    private Short incomeRequirement;   //소득분위(1~10)조건

    private String regionRequirement;   //지역 조건

    private String qualification;   //기타 자격 조건

    private String restriction;     //제한 조건

    private String requiredDocuments;   //필요서류

    private String benefit;     //우대조건

    private String selectionMethod; //선택방법(?)

    private String selectionQuota; //선택할당량(?)

    private Date startDate;     //신청시작일

    private Date endDate;       //신청마감일

    private URL URL;    //신청 사이트

}
