package com.grantip.backend.domain.user.entity;

import com.grantip.backend.global.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    // 현재 정보수정에 없는거는 유저네임, 비밀번호(변경따로있음), 성별. role, active는 어따쓰는지 보기
    // signupRequest 손보기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Column(name = "email", unique = true, nullable = false)
    @Email           // javax.validation.constraints.Email
    @NotBlank
    private String email;

    private String phone;

    private String current_school;

    private String high_school;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)     //many로 할거면 차라리 아래 특수를 하나씩 선택하게 하는건?
    @JoinColumn(name = "extra_info_id")
    private UserExtraInfo extraInfo;

    @Enumerated(EnumType.STRING)
    private UnivYear university_year;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address; //현재 거주 주소

    private String resident_address; //주민등록상 주소

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    private boolean active = true;




}
