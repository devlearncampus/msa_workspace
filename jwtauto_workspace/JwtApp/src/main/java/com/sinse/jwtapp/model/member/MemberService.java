package com.sinse.jwtapp.model.member;

import com.sinse.jwtapp.domain.Member;

public interface MemberService {
    public Member select(String id);
}
