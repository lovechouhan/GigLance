package com.minor.freelancing.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Razorpay {

    @Id
    private Long id;
    private String currency = "INR";
    private String receiptId = "order_rcptid_11";
    private int amount;

}
