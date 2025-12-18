package com.minor.freelancing.Entities;

import java.time.LocalDateTime;

import com.minor.freelancing.Helper.ProposalStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    private Double bidAmount;

    private Integer timelineDays;

    private String coverLetterUrl;      // Cloudinary URL
    private String coverLetterFileName;
    private String coverLetterFileType;

    

    @Builder.Default
    private ProposalStatus status = ProposalStatus.PENDING; // PENDING, ACCEPTED, REJECTED

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();



}
