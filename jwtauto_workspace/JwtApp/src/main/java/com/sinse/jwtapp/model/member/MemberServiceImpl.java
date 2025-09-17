package com.sinse.jwtapp.model.member;

import com.sinse.jwtapp.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    private final JpaMemberRepository repository;

    @Override
    public Member select(String id) {
        return repository.findById(id);
    }
}
