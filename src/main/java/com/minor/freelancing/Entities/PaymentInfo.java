package com.minor.freelancing.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.minor.freelancing.Helper.PaymentStatus;

@Entity
@Table(name = "payment_info")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod; // e.g., "Credit Card", "PayPal", "Razorpay"
    private PaymentStatus paymentStatus; // e.g., "Pending", "Completed", "Failed"
    private Double amount;
    private String transactionId; // e.g., Razorpay transaction id
    private String currency = "INR"; // 
    private String receiptId; // e.g., Razorpay receipt id
    private String orderId; // e.g., Razorpay order id

    


    

}
