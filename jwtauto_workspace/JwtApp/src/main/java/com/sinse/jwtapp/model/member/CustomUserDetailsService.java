package com.sinse.jwtapp.model.member;

import com.sinse.jwtapp.domain.CustomUserDetails;
import com.sinse.jwtapp.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final JpaMemberRepository jpaMemberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member=jpaMemberRepository.findById(username);
        if (member == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(member);
    }
}
