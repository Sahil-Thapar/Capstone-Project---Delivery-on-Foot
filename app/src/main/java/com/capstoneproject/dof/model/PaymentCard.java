package com.capstoneproject.dof.model;

/**
 * Created by mandeepkaur on 2017-11-23.
 */

public class PaymentCard {

    private String cardName;
    private String cardNumber;
    private String cvv;
    private String expiryDate;

    public PaymentCard(String nameOnCard, String cardNumber, String cvv, String expiryDate) {
        this.cardName = nameOnCard;
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    public PaymentCard(){

    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
