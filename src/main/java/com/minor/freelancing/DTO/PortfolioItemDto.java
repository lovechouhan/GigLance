package com.minor.freelancing.DTO;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItemDto {
    private Long id;
    private String title;
    private String description;
    private String mediaUrl;
    private Long freelancerId;
}
