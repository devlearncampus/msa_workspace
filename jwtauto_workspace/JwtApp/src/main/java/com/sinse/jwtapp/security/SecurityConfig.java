package com.sinse.jwtapp.security;

import com.sinse.jwtapp.model.member.CustomUserDetailsService;
import com.sinse.jwtapp.model.member.JpaMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JpaMemberRepository jpaMemberRepository;
    private final JwtBlacklistValidator jwtBlacklistValidator; // Redis 블랙리스트 검사기

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    // 1) 패스워드 인코더(회원가입/검증)
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    // 2) UserDetailsService: DB에서 사용자·권한 로딩
    @Bean
    public UserDetailsService userDetailsService(JpaMemberRepository jpaMemberRepository) {
        return new CustomUserDetailsService(jpaMemberRepository);
    }

    // 3) 로그인 시 인증에 사용할 Provider
    @Bean
    public AuthenticationManager authenticationManagerBean(CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        //AuthenticationManager는 DaoAuthenticationProvider를 통해
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService); //1) 유저 얻기(by id)
        provider.setPasswordEncoder(passwordEncoder);//2) 비번 검증(using PasswordEncoder)
        return new ProviderManager(provider);
    }

    // 5) JWT Decoder: 검증 자동 + (발행자/만료 기본검증) + 블랙리스트 추가검증
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();

        // 발행자 검증 + 기본 시계/만료 검증
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> composite = new DelegatingOAuth2TokenValidator<>(withIssuer, jwtBlacklistValidator);
        decoder.setJwtValidator(composite);

        return decoder;
    }

    // 6) SecurityFilterChain: 세션 미사용, 인증 규칙, 리소스서버(JWT 검증 자동)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                         // REST 방식이라 비활성화
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 무상태
                .cors(cors -> cors.configurationSource(corsSource())) // CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()  // 로그인은 허용
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()                                    // 나머지는 인증 필요
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults())); // JWT 검증은 스프링이 자동
        return http.build();
    }

    // 7) CORS 허용(프론트 개발용)
    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(allowedOrigins));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(Duration.ofHours(1));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}

