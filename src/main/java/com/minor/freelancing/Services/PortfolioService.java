package com.minor.freelancing.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.minor.freelancing.Entities.PortfolioItem;
import com.minor.freelancing.Repositories.PortfolioRepository;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public PortfolioItem create(PortfolioItem p) {
        return portfolioRepository.save(p);
    }

    public List<PortfolioItem> findByFreelancerId(Long id) {
        return portfolioRepository.findByFreelancerId(id);
    }

    public void deleteById(Long id) {
        portfolioRepository.deleteById(id);
    }
}
