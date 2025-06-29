package com.grantip.backend.domain.favorite.repository;

import com.grantip.backend.domain.favorite.entity.FavoriteScholar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteScholar, Long> {

}
