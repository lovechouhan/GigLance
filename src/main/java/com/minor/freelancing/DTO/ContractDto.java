package com.minor.freelancing.DTO;

import java.time.LocalDate;

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
public class ContractDto {
    private Long id;
    private Long proposalId;
    private Long clientId;
    private Long freelancerId;
    private Double amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
