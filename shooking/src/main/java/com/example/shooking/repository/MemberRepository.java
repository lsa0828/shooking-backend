package com.example.shooking.repository;

import com.example.shooking.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findOptionalByEmail(String email);

    boolean existsByEmail(String email);
}
