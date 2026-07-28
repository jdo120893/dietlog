package org.example.dietlog.security;

import lombok.RequiredArgsConstructor;
import org.example.dietlog.domain.RefreshToken;
import org.example.dietlog.domain.User;
import org.example.dietlog.repository.RefreshTokenRepository;
import org.example.dietlog.repository.UserRepository;
import org.example.dietlog.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public User signup(String email, String rawPassword, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("DUPLICATE_EMAIL");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .nickname(nickname)
                .build();

        return userRepository.save(user);
    }

    public TokenResponse login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        String accessToken = jwtTokenProvider.createToken(user.getId());
        String refreshTokenValue = jwtTokenProvider.createRefreshToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(14);

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .map(rt -> {
                    rt.updateToken(refreshTokenValue, expiresAt);
                    return rt;
                })
                .orElseGet(() -> RefreshToken.builder()
                        .user(user).token(refreshTokenValue).expiresAt(expiresAt).build());
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(accessToken, refreshTokenValue);
    }

    @Transactional
    public String reissueAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_REFRESH_TOKEN"));

        if (refreshToken.isExpired()) {
            throw new IllegalArgumentException("REFRESH_TOKEN_EXPIRED");
        }

        return jwtTokenProvider.createToken(refreshToken.getUser().getId());
    }

    public record TokenResponse(String accessToken, String refreshToken) {
    }
}