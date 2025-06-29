package com.grantip.backend.domain.user.entity;

import com.grantip.backend.domain.scholarship.entity.FavoriteScholar;
import com.grantip.backend.domain.scholarship.entity.ScholarCalender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserExtraInfo> userExtraInfoList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ScholarCalender> scholarCalenderList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FavoriteScholar> favoriteScholarList = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    private LocalDateTime updateTime;
}
