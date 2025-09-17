package com.sinse.jwtapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtBlacklistValidator implements OAuth2TokenValidator<Jwt> {

    private final StringRedisTemplate redis;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        // jti(고유 ID) 추출
        String jti = token.getId();
        if (jti == null) return OAuth2TokenValidatorResult.success();

        String key = "bl:" + jti;
        Boolean exists = redis.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            // Redis에 블랙리스트가 존재하면 -> 실패 처리
            OAuth2Error err = new OAuth2Error("invalid_token", "Token is blacklisted (logged out).", null);
            return OAuth2TokenValidatorResult.failure(err);
        }
        return OAuth2TokenValidatorResult.success();
    }
}
