package com.minor.freelancing.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreelancerRegistrationDto {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String designation;
    private String bio;
    private Double hourlyRate;
    // comma-separated skills
    private String skills;
}
