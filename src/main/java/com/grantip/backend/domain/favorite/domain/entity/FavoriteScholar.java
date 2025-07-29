package com.grantip.backend.domain.favorite.domain.entity;

import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.global.util.BaseEntity;
import com.grantip.backend.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteScholar extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "scholar_id")
    private Scholarship scholarship;

}
