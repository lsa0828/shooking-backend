package com.example.shooking.entity;

import com.example.shooking.dto.CardDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq_gen")
    @SequenceGenerator(
            name = "card_seq_gen",
            sequenceName = "card_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "expiration_date", nullable = false)
    private String expirationDate;

    @Column(nullable = false)
    private String cardholder;

    @Column(name = "security_code", nullable = false)
    private String securityCode;

    @Column(nullable = false)
    private String password;

    public Card(Member member, CardDTO dto) {
        this.member = member;
        this.cardNumber = dto.getCardNumber();
        this.expirationDate = dto.getExpirationDate();
        this.cardholder = dto.getCardholder();
        this.securityCode = dto.getSecurityCode();
        this.password = dto.getPassword();
    }

    public Card(Member member, String cardNumber, String expirationDate, String cardholder, String securityCode, String password) {
        this.member = member;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardholder = cardholder;
        this.securityCode = securityCode;
        this.password = password;
    }
}
