package com.minor.freelancing.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minor.freelancing.Entities.PortfolioItem;
import com.minor.freelancing.Services.PortfolioService;
import com.minor.freelancing.Services.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    public PortfolioController(PortfolioService portfolioService, UserService userService) {
        this.portfolioService = portfolioService;
        this.userService = userService;
    }

    // @PostMapping
    // public ResponseEntity<?> create(@RequestBody PortfolioItem p, HttpSession session) {
    //     Object uid = session.getAttribute("userId");
    //     if (!(uid instanceof Long))
    //         return ResponseEntity.status(401).build();
    //     Long userId = (Long) uid;
    //     userService.findById(userId).ifPresent(u -> {
    //         if (u instanceof Freelancer)
    //             p.setFreelancer((Freelancer) u);
    //     });
    //     var saved = portfolioService.create(p);
    //     return ResponseEntity.created(URI.create("/api/portfolio/" + saved.getId())).body(saved);
    // }

    @GetMapping("/freelancer/{id}")
    public ResponseEntity<List<PortfolioItem>> byFreelancer(@PathVariable Long id) {
        return ResponseEntity.ok(portfolioService.findByFreelancerId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        // ownership checks omitted for brevity
        portfolioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
