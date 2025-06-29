package com.grantip.backend.domain.user.entity;

import com.grantip.backend.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)     //many로 할거면 차라리 아래 특수를 하나씩 선택하게 하는건?
    @JoinColumn(name = "extra_info_id")
    private UserExtraInfo extraInfo;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String university;  //대학교

    private String highSchool;  //출신 고등학교

    @Enumerated(EnumType.STRING)
    private UnivYear univYear;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address; //현재 거주 주소

    private String resiAddress; //주민등록상 주소

    private Role role;

    @Builder.Default
    private boolean available = true;
}
