package com.minor.freelancing.DTO;

import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Projects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO {

    private Long id;
    private Freelancer freelancerName;
    private Client clientName;

    private Long projectId;
    private Projects projectTitle;
    private String message;
}
