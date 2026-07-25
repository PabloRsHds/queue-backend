package br.com.queue.service.login;

import br.com.queue.dtos.loginDto.RequestLoginDto;
import br.com.queue.dtos.loginDto.ResponseUserForLogin;
import br.com.queue.dtos.tokenDto.ResponseTokens;
import br.com.queue.entities.user.User;
import br.com.queue.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    // ========================================== LOGIN ==============================================================

    public ResponseTokens login(RequestLoginDto request, HttpServletResponse response) {

        // Faço uma verificação para ver se o usuário existe, e também verifico se o e-mail e a senha estão corretos
        var user = this.verifyUser(request.emailOrUsername(), request.password());

        // Retorno os tokens caso o usuário exista
        return this.generateTokens(user.userId(), user.role(), response);
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
    public ResponseTokens generateTokens(String userId, String role, HttpServletResponse response) {

        var expireToken = LocalDateTime.now().plusMinutes(10).toInstant(ZoneOffset.of("-03:00"));
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
        var refreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claimsRefresh)).getTokenValue();

        if (accessToken == null || refreshToken == null) {
            throw new JwtEncodingException("Unable to generate tokens");
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // durante o desenvolvimento
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new ResponseTokens(accessToken);
    }
    // ================================================================================================================



    // ======================================== REFRESH TOKENS ========================================================

    public ResponseTokens refreshTokens(String refreshToken, HttpServletResponse response) {

        var decodeRefreshToken = jwtDecoder.decode(refreshToken);

        User user = userRepository.findByUserId(decodeRefreshToken.getSubject())
                .orElseThrow();

        return generateTokens(
                user.getUserId(),
                user.getRole().name(),
                response
        );
    }
}
