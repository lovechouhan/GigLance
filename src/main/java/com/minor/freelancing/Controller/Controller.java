package com.minor.freelancing.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Services.ProjectService;
import com.minor.freelancing.Services.ProposalService;
import com.minor.freelancing.Services.UserService;

import jakarta.servlet.http.HttpSession;

@org.springframework.stereotype.Controller
public class Controller {

    private final UserService userService;
    private final ProjectService projectService;
    private final ProposalService proposalService;

    public Controller(UserService userService, ProjectService projectService, ProposalService proposalService) {
        this.userService = userService;
        this.projectService = projectService;
        this.proposalService = proposalService;
    }

    // @GetMapping("/login")
    // public String showLoginForm() {
    //     return "login";
    //}

    // @GetMapping("/register")
    // public String showRegisterForm() {
    //     return "register";
    // }

    @GetMapping("/base")
    public String showHome() {
        return "base";
    }

    @GetMapping("/dashboard1")
    public String showDashboard() {
        return "client/dashboard";
    }

    @GetMapping("/dashboard2")
    public String showDashboard2() {
        return "freelancer/dashboard";
    }

    @GetMapping("/main")
    public String getMethodName() {
        return "freelancer/main";
    }
    

    

   

    @PostMapping("/postproject")
    public String postProject(@RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String budget,
            @RequestParam(required = false) String deadline,
            @RequestParam(required = false) String category,
            HttpSession session) {
        Projects p = new Projects();
        p.setTitle(title);
        p.setDescription(description);
        p.setCategory(category);
        try {
            p.setBudget(Double.parseDouble(budget));
        } catch (Exception ignored) {
        }
        // attach client if logged in
       
        projectService.create(p);
        return "redirect:/post";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("projects", projectService.search(keyword));
        return "freelancer/search_results";
    }

    // @GetMapping("/proposals")
    // public String getProposals(HttpSession session, Model model) {
    //     Object uid = session.getAttribute("userId");
    //     if (!(uid instanceof Long)) {
    //         model.addAttribute("error", "Login required");
    //         return "login";
    //     }
    //     Long userId = (Long) uid;
    //     var userOpt = userService.findById(userId);
    //     if (userOpt.isPresent() && userOpt.get() instanceof com.minor.freelancing.Entities.Freelancer) {
    //         model.addAttribute("proposals", proposalService.findByFreelancerId(userId));
    //     } else {
    //         var projects = projectService.listByClient(userId);
    //         java.util.List<com.minor.freelancing.Entities.Proposal> all = new java.util.ArrayList<>();
    //         for (var pr : projects) {
    //             all.addAll(proposalService.findByProjectId(pr.getId()));
    //         }
    //         model.addAttribute("proposals", all);
    //     }
    //     return "freelancer/proposal";
    // }

    // @PostMapping("/projects/{projectId}/proposals")
    // public String submitProposal(@org.springframework.web.bind.annotation.PathVariable Long projectId,
    //         @RequestParam Double amount,
    //         @RequestParam Integer timelineDays,
    //         @RequestParam(required = false) String coverLetter,
    //         HttpSession session,
    //         Model model) {
    //     Object uid = session.getAttribute("userId");
    //     if (!(uid instanceof Long)) {
    //         model.addAttribute("error", "You must be logged in to submit a proposal");
    //         return "login";
    //     }
    //     Long userId = (Long) uid;
    //     var projectOpt = projectService.findById(projectId);
    //     if (projectOpt.isEmpty()) {
    //         model.addAttribute("error", "Project not found");
    //         return "freelancer/proposal";
    //     }

    //     Proposal prop = Proposal.builder()
    //             .project(projectOpt.get())
    //             .bidAmount(amount)
    //             .timelineDays(timelineDays)
    //             .coverLetter(coverLetter)
    //             .status("PENDING")
    //             .build();

    //     userService.findById(userId).ifPresent(u -> {
    //         if (u instanceof com.minor.freelancing.Entities.Freelancer) {
    //             prop.setFreelancer((com.minor.freelancing.Entities.Freelancer) u);
    //         }
    //     });

    //     proposalService.create(prop);
    //     return "redirect:/proposals";
   // }

    

    @GetMapping("/post")
    public String getSettings() {
        return "client/postproject";
    }

    @GetMapping("/myprojects")
    public String getMyProjects(HttpSession session, Model model) {
        Object uid = session.getAttribute("userId");
        if (uid instanceof Long) {
            Long userId = (Long) uid;
            model.addAttribute("projects", projectService.listByClient(userId));
        } else {
            model.addAttribute("projects", java.util.List.of());
        }
        return "client/myprojects";
    }

    // @GetMapping("/project/{id}")
    // public String viewProject(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
    //     var p = projectService.findById(id);
    //     if (p.isPresent()) {
    //         model.addAttribute("project", p.get());
    //         return "client/viewproposal"; // reuse viewproposal template to show project + proposals
    //     }
    //     return "redirect:/myprojects";
    // }

    @PostMapping("/project/{id}/delete")
    public String deleteProject(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session) {
        // TODO: in real app, check ownership
        projectService.deleteById(id);
        return "redirect:/myprojects";
    }

    @GetMapping("/viewproposal")
    public String getViewProposal() {
        return "client/viewproposal";
    }

    @GetMapping("activeprojects")
    public String getActiveProjects() {
        return "client/activeprojects";
    }

    @GetMapping("completedprojects")
    public String getCompletedProjects() {
        return "client/completedprojects";
    }

}
