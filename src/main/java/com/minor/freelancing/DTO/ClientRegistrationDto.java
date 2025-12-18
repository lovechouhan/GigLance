package com.minor.freelancing.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRegistrationDto {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String companyName;
    private String website;
    private String address;
}
