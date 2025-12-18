package com.minor.freelancing.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.minor.freelancing.Entities.Review;
import com.minor.freelancing.Repositories.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review create(Review r) {
        return reviewRepository.save(r);
    }

    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> findByRevieweeId(Long id) {
        return reviewRepository.findByRevieweeId(id);
    }

    public List<Review> findByReviewerId(Long id) {
        return reviewRepository.findByReviewerId(id);
    }
}
