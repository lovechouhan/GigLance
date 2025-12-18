package com.minor.freelancing.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     private String title;

     private boolean completed = false;

     @ManyToOne
     @JoinColumn(name = "freelancer_id")
     private Freelancer assignedFreelancer;

     @ManyToOne
     @JoinColumn(name = "project_id")
     @JsonIgnore
     private Projects project;

     private Long taskweightage = 0L;
     private Long taskprogress = 0L;
     private Boolean clientApproval = false;
     private String description;
     private String category;
}
