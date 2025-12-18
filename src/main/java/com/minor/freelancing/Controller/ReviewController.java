package com.minor.freelancing.Controller;

import com.minor.freelancing.Entities.Review;
import com.minor.freelancing.Services.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Review r, HttpSession session) {
        // in real app validate reviewer matches session
        var saved = reviewService.create(r);
        return ResponseEntity.created(URI.create("/api/reviews/" + saved.getId())).body(saved);
    }

    @GetMapping("/reviewee/{id}")
    public ResponseEntity<?> byReviewee(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findByRevieweeId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return reviewService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
