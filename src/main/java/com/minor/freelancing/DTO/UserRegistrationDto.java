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
public class UserRegistrationDto {

    private String name;
    private String email;
    private String password;
    private String role; // CLIENT or FREELANCER
    private String phone;
}
