package com.grantip.backend.domain.favorite.repository;

import com.grantip.backend.domain.favorite.domain.entity.FavoriteScholar;
import com.grantip.backend.domain.scholarship.domain.entity.Scholarship;
import com.grantip.backend.domain.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteScholar, Long> {
  Optional<FavoriteScholar> findByScholarshipAndUser(Scholarship scholarship, User user);

  Page<FavoriteScholar> findByUser(User user, Pageable pageable);
}
