package com.grantip.backend.domain.token.service;

import com.grantip.backend.domain.token.domain.entity.RefreshToken;
import com.grantip.backend.domain.token.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(String email, String token) {
        refreshTokenRepository.findByEmail(email)
                .ifPresentOrElse(
                        existingToken -> {
                            existingToken.setToken(token);
                            refreshTokenRepository.save(existingToken);
                        },
                        () -> {
                            RefreshToken refreshToken = RefreshToken.builder()
                                    .email(email)
                                    .token(token)
                                    .build();
                            refreshTokenRepository.save(refreshToken);
                        }
                );
    }

    @Transactional
    public String getRefreshToken(String email) {
        return refreshTokenRepository.findByEmail(email)
                .map(RefreshToken::getToken)
                .orElse(null);
    }

    @Transactional
    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}



