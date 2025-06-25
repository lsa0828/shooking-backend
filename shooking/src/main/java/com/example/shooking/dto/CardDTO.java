package com.example.shooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private Long id;
    private String cardNumber;
    private String expirationDate;
    private String cardholder;
    private String securityCode;
    private String password;

    public CardDTO(String cardNumber, String expirationDate, String cardholder, String securityCode, String password) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardholder = cardholder;
        this.securityCode = securityCode;
        this.password = password;
    }
}
