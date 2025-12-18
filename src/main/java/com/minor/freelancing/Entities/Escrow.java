package com.minor.freelancing.Entities;

import java.time.LocalDateTime;

import com.minor.freelancing.Helper.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.Setter;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Escrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Proposal proposal;

    @ManyToOne
    private Projects project;

    @ManyToOne
    private Client client;
    @ManyToOne
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    private Double amount;

    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL)
    @JoinColumn(name = "payment_info_id")
    private PaymentInfo paymentInfo;

    @lombok.Builder.Default
    private PaymentStatus status = PaymentStatus.NOT_INITIATED; // HELD, RELEASED, REFUNDED

    @lombok.Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
