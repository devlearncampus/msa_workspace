package com.sinse.jwtapp.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.access-minutes}")
    private long accessMinutes;

    // AccessToken 발급 (검증은 Resource Server가 자동)
    public Map<String, Object> generateAccessToken(Authentication auth) {
        // 권한을 space-separated scope 로 넣기 (표준 관례)
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessMinutes * 60);

        // 고유 식별자(jti) 생성 -> 로그아웃 블랙리스트에 사용
        String jti = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(exp)
                .subject(auth.getName())
                .id(jti)                          // jti
                .claim("scope", scope)            // 권한
                .build();

        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        Map<String,Object> result = new HashMap<>();
        result.put("accessToken", token);
        result.put("tokenType", "Bearer");
        result.put("expiresAt", exp.toEpochMilli());
        result.put("jti", jti);
        return result;
    }
}

