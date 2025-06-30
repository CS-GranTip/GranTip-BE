package com.grantip.backend.domain.token.service;

import com.grantip.backend.domain.token.entity.RefreshToken;
import com.grantip.backend.domain.token.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(String loginId, String token) {
        refreshTokenRepository.findByLoginId(loginId)
                .ifPresentOrElse(
                        existingToken -> {
                            existingToken.setToken(token);
                            refreshTokenRepository.save(existingToken);
                        },
                        () -> {
                            RefreshToken refreshToken = RefreshToken.builder()
                                    .loginId(loginId)
                                    .token(token)
                                    .build();
                            refreshTokenRepository.save(refreshToken);
                        }
                );
    }

    @Transactional
    public String getRefreshToken(String loginId) {
        return refreshTokenRepository.findByLoginId(loginId)
                .map(RefreshToken::getToken)
                .orElse(null);
    }

    @Transactional
    public void deleteRefreshToken(String loginId) {
        refreshTokenRepository.deleteByLoginId(loginId);
    }
}



