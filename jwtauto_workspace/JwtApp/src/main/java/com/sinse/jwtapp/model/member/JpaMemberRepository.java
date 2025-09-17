package com.sinse.jwtapp.model.member;

import com.sinse.jwtapp.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberRepository extends JpaRepository<Member, Integer> {
    public Member findById(String id);
}
