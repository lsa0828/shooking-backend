package com.example.shooking.dto;

import com.example.shooking.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String cardNumber;
    private String expirationDate;
    private String cardholder;

    public CardResponse(Card card) {
        this.id = card.getId();
        this.cardNumber = card.getCardNumber();
        this.expirationDate = card.getExpirationDate();
        this.cardholder = card.getCardholder();
    }
}
