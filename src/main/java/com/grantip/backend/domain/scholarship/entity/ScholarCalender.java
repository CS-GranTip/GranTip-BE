package com.grantip.backend.domain.scholarship.entity;

import com.grantip.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarCalender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "scholar_id")
    private Scholarship  scholarship;

    private String memo;

    @Builder.Default
    private LocalDateTime createTime = LocalDateTime.now();

    private LocalDateTime updateTime;
}
