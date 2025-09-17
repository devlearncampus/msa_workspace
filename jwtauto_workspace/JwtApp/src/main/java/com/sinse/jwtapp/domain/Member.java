package com.sinse.jwtapp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int memberId;

    @Column(nullable=false, unique = true)
    private String id;       // 로그인 ID

    @Column(nullable=false)
    private String password;       // BCrypt 해시

    private String email;

    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
}
