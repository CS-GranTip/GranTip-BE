package com.grantip.backend.domain.token.repository;

import com.grantip.backend.domain.token.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByEmail(String email);
    void deleteByEmail(String email);
}

