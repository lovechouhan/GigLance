package com.minor.freelancing.DTO;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.minor.freelancing.Helper.ProposalStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;


import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Projects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalDto {
    private Long id;
    private Projects project;
    private Freelancer freelancer;
    private Double bidAmount;
    private Integer timelineDays;
    private MultipartFile  coverLetter;
    private ProposalStatus status;
    private LocalDateTime createdAt;
    private String coverLetterFileName;
    private String coverLetterFileType;
    private String coverLetterUrl;
    // Presentation / view helper fields (populated by controller)
    private String projectTitle;
    // Some templates reference 'title' directly; provide a convenience alias
    private String title;
    private String clientName;
    private String statusIcon;
}
