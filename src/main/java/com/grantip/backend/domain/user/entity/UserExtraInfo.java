package com.grantip.backend.domain.user.entity;

import com.grantip.backend.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExtraInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "extraInfo")
    private User user;

    @Builder.Default
    private boolean disabled = false;       //장애여부

    @Builder.Default
    private boolean multiChild = false;     //다자녀가정

    @Builder.Default
    private boolean nationalMerit = false;  //국가 유공, 보훈해당자

    @Builder.Default
    private boolean singleParent = false;   //한부모가정

    @Builder.Default
    private boolean orphan = false;         //소년,소녀가장

    @Builder.Default
    private boolean low_income = false;     //저소득층

    @Builder.Default
    private boolean multiCulture = false;   //다문화

    @Builder.Default
    private boolean Defector = false;       //북한이탈주민

    @Builder.Default
    private boolean talent = false;         //특기자

    private float highSchoolGrade;  //고등학교 성적

    private float universityGrade;  //대학성적

}

