package com.katalogizegroup.katalogize.services;

import com.katalogizegroup.katalogize.models.RefreshToken;
import com.katalogizegroup.katalogize.repositories.RefreshTokenRepository;
import com.katalogizegroup.katalogize.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${app.tokenRefreshExpirationMsec}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                userId,
                Instant.now().plusMillis(refreshTokenDurationMs)
        );
        refreshTokenRepository.deleteByUserId(userId);
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " - Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public String deleteByUserId(String userId) {
        return refreshTokenRepository.deleteByUserId(userId).get().getToken();
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

}
