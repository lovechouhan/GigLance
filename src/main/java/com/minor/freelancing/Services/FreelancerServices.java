package com.minor.freelancing.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Repositories.FreelancerRepository;

@Service
public class FreelancerServices {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EscrowServices escrowServices;

    public Freelancer findByEmail(String email) {
        return freelancerRepository.findByEmail(email);
    }
    public Freelancer findById(Long id) {
        Freelancer freelancer = freelancerRepository.findById(id).orElse(null);
        if (freelancer != null) {
            return freelancer;
        }
        return null; // or throw an exception if preferred
    }

    public Double calculateAverageRating(Long id) {
        Freelancer freelancer = freelancerRepository.findById(id).orElse(null);
        if (freelancer != null) {
            Double rating = freelancer.getRating();
            return rating != null ? rating : 0.0;
        }
        return 0.0;
    }

    public String calculateTotalEarnings(Long id) {
        Freelancer freelancer = freelancerRepository.findById(id).orElse(null);
        if (freelancer != null) {
            String earnings = escrowServices.getTotalEarningsByFreelancerId(id);
            if (earnings != null) {
                return earnings;
            }
            return "0";
        }
        return "0";
    }
    public void save(Freelancer existingFreelancer) {
        freelancerRepository.save(existingFreelancer);
    }
}
