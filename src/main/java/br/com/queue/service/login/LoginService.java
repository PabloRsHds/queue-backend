package br.com.queue.service.login;

import br.com.queue.dtos.loginDto.RequestLoginDto;
import br.com.queue.dtos.loginDto.ResponseUserForLogin;
import br.com.queue.dtos.tokenDto.RequestTokensDto;
import br.com.queue.dtos.tokenDto.ResponseTokens;
import br.com.queue.entities.user.User;
import br.com.queue.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    // ========================================== LOGIN ==============================================================

    public ResponseTokens login(RequestLoginDto request) {

        // Faço uma verificação para ver se o usuário existe, e também verifico se o e-mail e a senha estão corretos
        var user = this.verifyUser(request.emailOrUsername(), request.password());

        // Retorno os tokens caso o usuário exista
        return this.generateTokens(user.userId(), user.role());
    }

    public ResponseUserForLogin verifyUser(String emailOrUsername, String password) {

        User user = userRepository.findByEmailOrUsername(emailOrUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Password is incorrect");
        }

        return new ResponseUserForLogin(
                user.getUserId(),
                null,
                user.getRole().name()
        );
    }

    // Metodo de geração de tokens e refreshTokens
    public ResponseTokens generateTokens(String userId, String role) {

        var expireToken = LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("QUEUE-LOGIN")
                .issuedAt(now)
                .subject(userId)
                .expiresAt(expireToken)
                .claim("SCOPE", role)
                .build();

        var expireRefreshToken = LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00"));

        var claimsRefresh = JwtClaimsSet.builder()
                .issuer("QUEUE-LOGIN")
                .issuedAt(now)
                .subject(userId)
                .expiresAt(expireRefreshToken)
                .claim("SCOPE", role)
                .build();

        var accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        var accessRefreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claimsRefresh)).getTokenValue();

        if (accessToken == null || accessRefreshToken == null) {
            throw new JwtEncodingException("Unable to generate tokens");
        }

        return new ResponseTokens(accessToken, accessRefreshToken);
    }
    // ================================================================================================================



    // ======================================== REFRESH TOKENS ========================================================

    public ResponseTokens refreshTokens(RequestTokensDto request) {

        var accessToken = jwtDecoder.decode(request.accessToken());
        var refreshToken = jwtDecoder.decode(request.refreshToken());

        // 1. Refresh token deve expirar
        if (refreshToken.getExpiresAt() == null ||
                Instant.now().isAfter(refreshToken.getExpiresAt())) {

            throw new RuntimeException("Invalid or expired refresh token");
        }

        // 2. Subjects devem bater
        if (!Objects.equals(refreshToken.getSubject(), accessToken.getSubject())) {

            throw new RuntimeException("Invalid refresh token");
        }

        // 3. Busca usuário confiável
        Optional<User> user = this.userRepository.findByUserId(refreshToken.getSubject());

        if (user.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        return this.generateTokens(user.get().getUserId(), user.get().getRole().name());
    }
}
