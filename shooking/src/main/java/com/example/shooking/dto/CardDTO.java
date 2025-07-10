package com.example.shooking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {
    private Long id;

    @NotBlank(message = "카드번호는 필수입니다.")
    private String cardNumber;

    @NotBlank(message = "만료일은 필수입니다.")
    private String expirationDate;

    @NotBlank(message = "카드 소유자는 필수입니다.")
    private String cardholder;

    @NotBlank(message = "보안코드는 필수입니다.")
    private String securityCode;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    public CardDTO(String cardNumber, String expirationDate, String cardholder, String securityCode, String password) {
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cardholder = cardholder;
        this.securityCode = securityCode;
        this.password = password;
    }
}
