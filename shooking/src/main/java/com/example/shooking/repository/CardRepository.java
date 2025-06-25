package com.example.shooking.repository;

import com.example.shooking.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);
}
