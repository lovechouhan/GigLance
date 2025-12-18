package com.minor.freelancing.Entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "clientBuilder")

public class Client {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String password;

    private String ImageUrl;

    private String phone;

    @Column(length = 2000)
    private String companyName;

    private LocalDate memberSince;

    private String address;

    private String website;

    @Column(length = 2000)
    private String bio;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
