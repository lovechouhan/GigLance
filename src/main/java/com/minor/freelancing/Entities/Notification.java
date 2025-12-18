package com.minor.freelancing.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // client or freelancer dono ke liye
    private String userType; // "CLIENT" or "FREELANCER"

    private String title;
    private String message;

    private boolean readStatus = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private Long relatedSubmissionId; // To link notification to a specific submission (if needed)
}
