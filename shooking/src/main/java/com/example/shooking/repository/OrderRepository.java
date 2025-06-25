package com.example.shooking.repository;

import com.example.shooking.dto.OrderDTO;
import com.example.shooking.entity.OrderSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderSheet, Long> {
    List<OrderDTO> findByMemberId(Long memberId);
}
