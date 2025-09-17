package com.sinse.jwtapp.controller;

import com.sinse.jwtapp.domain.CustomUserDetails;
import com.sinse.jwtapp.domain.Member;
import com.sinse.jwtapp.model.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;

    // JWT 검증 후 SecurityContext에 담긴 사용자 정보 접근
    @GetMapping("/me")
    public Map<String,Object> me(@AuthenticationPrincipal Jwt jwt) {
        Member member=memberService.select(jwt.getSubject());

        return Map.of(
                "id", member.getId(),
                "authorities", member.getRole().getRole_name()
        );
    }
}
