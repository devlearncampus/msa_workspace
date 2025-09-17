package com.sinse.jwtapp.controller;



import com.sinse.jwtapp.controller.dto.LoginRequest;
import com.sinse.jwtapp.domain.CustomUserDetails;
import com.sinse.jwtapp.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;  // 로그인 인증 처리
    private final JwtTokenService tokenService;       // AccessToken 발급
    private final StringRedisTemplate redis;          // 블랙리스트 저장
    private final JwtDecoder jwtDecoder;              // 토큰 디코드(검증 포함)

    // --- 로그인: username/password -> AccessToken 발급 ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        log.debug("로그인 요청 받음, id="+req.getUsername()+", password="+req.getPassword());

        // 1) 스프링 인증 매니저로 사용자 인증 (비밀번호 검증 포함)
        //실패시 예외 발생
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        CustomUserDetails customUserDetails=(CustomUserDetails)auth.getPrincipal();
        log.debug("인증 성공"+customUserDetails.getUsername());

        // 2) 인증 성공 -> AccessToken 발급 (검증은 리소스 서버가 자동)
        Map<String, Object> token = tokenService.generateAccessToken(auth);

        return ResponseEntity.ok(token);
    }

    // --- 로그아웃: 현재 Bearer 토큰을 블랙리스트에 등록 ---
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization) {
        // ex) "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing Bearer token"));
        }
        String token = authorization.substring(7);

        // 1) 토큰을 디코드(검증 포함)하여 jti/exp 추출
        Jwt jwt = jwtDecoder.decode(token); // 유효하지 않으면 여기서 예외
        String jti = jwt.getId();
        Instant exp = jwt.getExpiresAt();

        if (jti == null || exp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token (no jti/exp)"));
        }

        // 2) 만료시각까지 남은 TTL 계산해서 Redis 블랙리스트에 저장
        long ttlSeconds = Math.max(1, exp.getEpochSecond() - Instant.now().getEpochSecond());
        String key = "bl:" + jti;
        redis.opsForValue().set(key, "1", Duration.ofSeconds(ttlSeconds));

        // 3) (선택) SecurityContext 비우기
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(Map.of("result", "logged out", "jti", jti, "ttlSeconds", ttlSeconds));
    }

}
