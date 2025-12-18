package com.minor.freelancing.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.minor.freelancing.Helper.ContractStatus;
import com.minor.freelancing.Helper.PaymentStatus;

@Entity
@Table(name = "contracts")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Projects project;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    private Double amount;

    private LocalDate startDate;
    private LocalDate endDate;

    private ContractStatus status = ContractStatus.NOT_STARTED; // ACTIVE, COMPLETED, CANCELLED

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDate deadline ;

    private String paymentMethod = "RAZORPAY";
    private PaymentStatus paymentStatus = PaymentStatus.NOT_INITIATED;

    private String clientSignToken;
    private String freelancerSignToken;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String clientSignature;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String freelancerSignature;

    private LocalDateTime clientSignedAt;
    private LocalDateTime freelancerSignedAt;


}
