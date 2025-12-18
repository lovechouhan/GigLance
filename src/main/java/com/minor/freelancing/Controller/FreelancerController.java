package com.minor.freelancing.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minor.freelancing.CommonMethod.CommonMethodService;
import com.minor.freelancing.DTO.ProposalDto;
import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Contract;
import com.minor.freelancing.Entities.Escrow;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Invitations;
import com.minor.freelancing.Entities.Notification;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Entities.Review;
import com.minor.freelancing.Entities.Task;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Entities.WorkSubmission;
import com.minor.freelancing.Helper.ContractStatus;
import com.minor.freelancing.Helper.PaymentStatus;
import com.minor.freelancing.Repositories.TaskRepository;
import com.minor.freelancing.Services.ClientServices;
import com.minor.freelancing.Services.ContractService;
import com.minor.freelancing.Services.EscrowServices;

import com.minor.freelancing.Services.FreelancerServices;
import com.minor.freelancing.Services.GeminiService;
import com.minor.freelancing.Services.InvitationServices;

import com.minor.freelancing.Services.JobSearchServices;
import com.minor.freelancing.Services.NotificationService;
import com.minor.freelancing.Services.ProjectService;
import com.minor.freelancing.Services.ProposalService;
import com.minor.freelancing.Services.ReviewService;
import com.minor.freelancing.Services.UserService;
import com.minor.freelancing.Services.WorkSubmissionService;

@Controller
@RequestMapping("/freelancer")
public class FreelancerController {

    private final Logger logger = LoggerFactory.getLogger(FreelancerController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ClientServices clientServices;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FreelancerServices freelancerService;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private InvitationServices invitationService;

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommonMethodService commonMethodService;

    @Autowired
    private WorkSubmissionService submissionService;

    @Autowired
    private JobSearchServices jobSearchServices;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private EscrowServices escrowServices;

    @GetMapping("/dashboard/{id}")
    public String dashboard(@PathVariable("id") Long id, Model model) {
        Freelancer freelancer = freelancerService.findById(id);
        if (freelancer == null) {
            logger.error("Freelancer with ID " + id + " not found.");
            return "freelancer/dashboard"; // return an error view or handle accordingly
        }
        int Earnings = 0; // Replace with actual calculation if needed
        Double rating = freelancerService.calculateAverageRating(freelancer.getId());
        int activeProjects = projectService.countActiveProjectsByFreelancerId(freelancer.getId());
        int TotalProjects = projectService.getAllProjectsByFreelancerId(freelancer.getId()).size();
        int ProposalsSent = proposalService.countProposalsByFreelancerId(freelancer.getId());
        int InvitationsReceived = invitationService.countInvitationsByFreelancerId(freelancer.getId());
        List<Projects> activeProjectsList = projectService.getActiveProjectsByFreelancerId(freelancer.getId());
        List<String> weekDays = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        List<Integer> weekEarnings = List.of(250, 300, 150, 500, 200, 100, 400);
        Long unreadNotifications = notificationService.getfreelancerUnreadCount(freelancer.getId());
        List<Review> reviews = reviewService.findByRevieweeId(freelancer.getId());
        model.addAttribute("weekDays", weekDays);
        model.addAttribute("weekEarnings", weekEarnings);
        model.addAttribute("weekly", List.of(1500, 1800, 900, 2300, 2000, 2500, 1700));
        model.addAttribute("TotalProjects", TotalProjects);
        model.addAttribute("activeProjects", activeProjects);
        model.addAttribute("activeProjectsList", activeProjectsList);
        model.addAttribute("Earnings", Earnings);
        model.addAttribute("rating", rating);
        model.addAttribute("ProposalsSent", ProposalsSent);
        model.addAttribute("InvitationsReceived", InvitationsReceived);
        model.addAttribute("unreadNotifications", unreadNotifications);
        model.addAttribute("reviews", reviews);
        // int Earnings = .calculateTotalEarnings(freelancer.getId());

        // model.addAttribute("Earnings", Earnings);
        model.addAttribute("freelancer", freelancer);
        return "freelancer/dashboard";
    }

    @GetMapping("/reviews/{id}")
    public String allReviews(@PathVariable("id") Long id, Model model) {
        Freelancer freelancer = freelancerService.findById(id);
        if (freelancer == null) {
            return "redirect:/freelancer/dashboard/" + id;
        }

        List<Review> reviews = reviewService.findByRevieweeId(id);
        Double averageRating = freelancerService.calculateAverageRating(id);
        int totalReviews = reviews != null ? reviews.size() : 0;

        model.addAttribute("freelancer", freelancer);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("totalReviews", totalReviews);
        return "freelancer/allReviews";
    }

    // Review API endpoints
    @PostMapping("/api/reviews")
    @ResponseBody
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        Review saved = reviewService.create(review);
        return ResponseEntity.created(java.net.URI.create("/api/reviews/" + saved.getId())).body(saved);
    }

    @GetMapping("/api/reviews/reviewee/{id}")
    @ResponseBody
    public ResponseEntity<?> getReviewsByReviewee(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findByRevieweeId(id));
    }

    @GetMapping("/api/reviews/{id}")
    @ResponseBody
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        return reviewService.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /// objects.nonnull
    @GetMapping("/findProjects")
    public String findProjects(Model model) {
        List<Projects> projects = projectService.getAllProjectsOPEN();
        if (projects != null && !projects.isEmpty()) {
            System.out.println("Projects found: " + projects.size()); // add a logger instead of sysout
        } else {
            System.out.println("No projects found.");
        }

        model.addAttribute("projects", projects);
        return "freelancer/searchProjects";
    }

    @GetMapping("/jobs/live")
    public String liveJobs(Model model) {

        model.addAttribute(
                "jobs",
                jobSearchServices.fetchLiveJobs());

        return "freelancer/livejobs"; // thymeleaf file
    }

    @GetMapping("/dashboard2")
    public String freelancerDashboard(Model model) {

        User loggedUser = commonMethodService.AuthenticateUser();
        if (loggedUser == null) {
            return "redirect:/login"; // or some error page
        }

        return "freelancer/dashboard";
    }

    @GetMapping("/viewProjectDetails/{id}")
    public String viewProjectDetail(@PathVariable("id") Long id, Model model) throws Exception {
        Projects project = projectService.getProjectById(id);
        if (project == null) {
            throw new Exception("Project with ID " + id + " not found."); // return an error view or handle accordingly
        }
        model.addAttribute("project", project);
        return "Projects/Details";
    }

    @GetMapping({ "/myprojects/{id}" })
    public String viewProposals(@PathVariable("id") Long freelancerId, Model model) {
        Freelancer freelancer = freelancerService.findById(freelancerId);
        if (freelancer == null) {
            logger.error("Freelancer with ID " + freelancerId + " not found.");
            return "freelancer/viewprojects"; // return an error view or handle accordingly
        }
        List<Projects> projects = projectService.getAllProjectsByFreelancerId(freelancer.getId());
        System.out.println("Projects in viewProposals: " + (projects == null ? 0 : projects.size()));
        model.addAttribute("projects", projects);
        // template file is freelancer/viewproposal.html
        return "freelancer/viewprojects";

    }

    @GetMapping("/viewproposal/{id}")
    public String viewProposal(@PathVariable("id") Long id, Model model) {
        try {
            Freelancer freelancer = freelancerService.findById(id);
            if (freelancer == null) {
                logger.error("Freelancer with ID " + id + " not found.");
            }
            List<Proposal> proposals = proposalService.findProposalByFreelancerId(freelancer.getId());
            if (proposals == null || proposals.isEmpty()) {
                System.out.println("No proposals found for freelancer ID: " + id);
            } else {
                System.out.println("Proposals found: " + proposals.size());
            }
            model.addAttribute("proposals", proposals);
            return "freelancer/proposal";
        } catch (Exception e) {
            logger.error("Error retrieving proposals for freelancer ID " + id, e);
            return "freelancer/proposal";
        }
    }

    @GetMapping("/activeprojects")
    public String activeProjects() {
        return "freelancer/activeProjects";
    }

    @GetMapping("/completedProjects/{id}")
    public String completedProjects(@PathVariable("id") Long id, Model model) {
        Freelancer freelancer = freelancerService.findById(id);
        if (freelancer == null) {
            logger.error("Freelancer with ID " + id + " not found.");
            return "freelancer/completed"; // return an error view or handle accordingly
        }
        List<Projects> completedProjects = projectService.getCompletedProjectsByFreelancerId(freelancer.getId());
        model.addAttribute("completedProjects", completedProjects);
        return "freelancer/completed";
    }

    @GetMapping("/earnings/{id}")
    public String earnings(@PathVariable("id") Long id, Model model) {
        Freelancer freelancer = freelancerService.findById(id);
        if (freelancer == null) {
            logger.error("Freelancer with ID " + id + " not found.");
            return "freelancer/earn"; // return an error view or handle accordingly
        }
        List<Projects> projects = projectService.getAllProjectsByFreelancerId(freelancer.getId());
        List<Escrow> payments = escrowServices.getEscrowsByFreelancerId(freelancer.getId());

        double totalEarnings = payments.stream()
                .filter(e -> e.getPaymentInfo() != null
                        && e.getPaymentInfo().getPaymentStatus() == PaymentStatus.COMPLETED)
                .mapToDouble(e -> e.getAmount() != null
                        ? e.getAmount()
                        : (e.getPaymentInfo() != null && e.getPaymentInfo().getAmount() != null
                                ? e.getPaymentInfo().getAmount()
                                : 0.0))
                .sum();

        double pendingPayments = payments.stream()
                .filter(e -> e.getPaymentInfo() != null && e.getPaymentInfo().getPaymentStatus() == PaymentStatus.HOLD)
                .mapToDouble(e -> e.getAmount() != null
                        ? e.getAmount()
                        : (e.getPaymentInfo() != null && e.getPaymentInfo().getAmount() != null
                                ? e.getPaymentInfo().getAmount()
                                : 0.0))
                .sum();

        double availableBalance = Math.max(0, totalEarnings - pendingPayments);

        long completedTransactions = payments.stream()
                .filter(e -> e.getPaymentInfo() != null
                        && e.getPaymentInfo().getPaymentStatus() == PaymentStatus.COMPLETED)
                .count();

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(6);
        LocalDate startOfMonth = today.withDayOfMonth(1);

        double todayEarnings = payments.stream()
                .filter(e -> e.getCreatedAt() != null && e.getCreatedAt().toLocalDate().isEqual(today))
                .mapToDouble(e -> e.getAmount() != null
                        ? e.getAmount()
                        : (e.getPaymentInfo() != null && e.getPaymentInfo().getAmount() != null
                                ? e.getPaymentInfo().getAmount()
                                : 0.0))
                .sum();

        double weekEarnings = payments.stream()
                .filter(e -> e.getCreatedAt() != null && !e.getCreatedAt().toLocalDate().isBefore(startOfWeek))
                .mapToDouble(e -> e.getAmount() != null
                        ? e.getAmount()
                        : (e.getPaymentInfo() != null && e.getPaymentInfo().getAmount() != null
                                ? e.getPaymentInfo().getAmount()
                                : 0.0))
                .sum();

        double monthEarnings = payments.stream()
                .filter(e -> e.getCreatedAt() != null && !e.getCreatedAt().toLocalDate().isBefore(startOfMonth))
                .mapToDouble(e -> e.getAmount() != null
                        ? e.getAmount()
                        : (e.getPaymentInfo() != null && e.getPaymentInfo().getAmount() != null
                                ? e.getPaymentInfo().getAmount()
                                : 0.0))
                .sum();

        List<Escrow> recentEarnings = payments.stream()
                .sorted(Comparator.comparing(Escrow::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("freelancer", freelancer);
        model.addAttribute("projects", projects);
        model.addAttribute("totalEarnings", totalEarnings);
        model.addAttribute("pendingPayments", pendingPayments);
        model.addAttribute("completedTransactions", completedTransactions);
        model.addAttribute("availableBalance", availableBalance);
        model.addAttribute("todayEarnings", todayEarnings);
        model.addAttribute("weekEarnings", weekEarnings);
        model.addAttribute("monthEarnings", monthEarnings);
        model.addAttribute("recentEarnings", recentEarnings);
        return "freelancer/earn";
    }

    @GetMapping("/sendProposals/{id}")
    public String sendProposals(@PathVariable("id") Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }

        Freelancer freelancer = freelancerService.findByEmail(email);
        model.addAttribute("freelancer", freelancer);

        Projects project = projectService.findById(id);
        // add project to model so the form can show project details
        model.addAttribute("project", project);
        model.addAttribute("proposaldto", new ProposalDto());
        // projectService.updateProjectStatus(project);
        return "freelancer/proposalform";
    }

    @GetMapping("/sendProposals/{id}/{invitationId}")
    public String sendProposals2(@PathVariable("id") Long id, @PathVariable("invitationId") Long invitationId,
            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }

        Freelancer freelancer = freelancerService.findByEmail(email);
        model.addAttribute("freelancer", freelancer);

        Projects project = projectService.findById(id);
        Invitations invitation = invitationService.findById(invitationId);
        invitationService.updateInviteStatus(invitation, project);
        model.addAttribute("invitation", invitation);
        // add project to model so the form can show project details
        model.addAttribute("project", project);
        model.addAttribute("proposaldto", new ProposalDto());
        // projectService.updateProjectStatus(project);
        return "freelancer/proposalform";
    }

    @GetMapping("/invitations/{id}")
    public String viewInvitations(@PathVariable("id") Long freelancerId, Model model) {
        Freelancer freelancer = freelancerService.findById(freelancerId);
        if (freelancer == null) {
            logger.error("Freelancer with ID " + freelancerId + " not found.");
            System.out.println("Freelancer with ID " + freelancerId + " not found.");
            return "freelancer/invitations"; // return an error view or handle accordingly
        }

        List<Invitations> invitations = invitationService.getInvitationsByFreelancerId(freelancer.getId());
        model.addAttribute("invitations", invitations);
        System.out.println("Invitations found: " + invitations);
        return "freelancer/invitations";

    }

    @GetMapping("/projects/viewdetails/{id}")
    public String viewProjectDetails(@PathVariable("id") Long id, Model model) {
        Projects project = projectService.getProjectById(id);
        if (project == null) {
            logger.error("Project with ID " + id + " not found.");
            return "error"; // return an error view or handle accordingly
        }
        model.addAttribute("project", project);
        return "freelancer/projectDetail";
    }

    @GetMapping("/project/{id}")
    public String viewProjectWork(@PathVariable("id") Long id, Model model) {
        Projects project = projectService.getProjectById(id);
        if (project == null) {
            logger.error("Project with ID " + id + " not found.");
            return "error";
        }
        List<Task> tasks = taskRepository.findByProjectId(id);
        model.addAttribute("project", project);
        model.addAttribute("tasks", tasks);
        logger.info("✅ Loading project work view for ID: " + id);
        return "freelancer/projectDetail";
    }

    @GetMapping("/project/details/{id}")
    public String viewProjectDetailsInfo(@PathVariable("id") Long id, Model model) {
        Projects project = projectService.getProjectById(id);
        if (project == null) {
            logger.error("Project with ID " + id + " not found.");
            return "error";
        }
        model.addAttribute("project", project);
        logger.info("✅ Loading project details view for ID: " + id);
        return "freelancer/projectDetail";
    }

    /**
     * Simple AI summary endpoint used by the frontend. Returns a small JSON object
     * with a generated summary for the given project id. This is a placeholder
     * implementation — replace with a real AI service integration as needed.
     */
    @GetMapping("/ai-summary/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAiSummary(@PathVariable("id") Long id) {
        Projects project = projectService.getProjectById(id);
        Map<String, String> resp = new HashMap<>();
        if (project == null) {
            resp.put("summary", "Project not found.");
            return ResponseEntity.status(404).body(resp);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(project.getTitle() != null ? project.getTitle() : "Untitled");
        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            sb.append(" — ")
                    .append(project.getDescription().length() > 160 ? project.getDescription().substring(0, 160) + "..."
                            : project.getDescription());
        }
        if (project.getBudget() != null) {
            sb.append(" Budget: ₹").append(project.getBudget());
        }

        // Add lightweight guidance / tags
        sb.append("\n\nSuggested tags: ");
        if (project.getCategory() != null)
            sb.append(project.getCategory());
        else
            sb.append("general");

        resp.put("summary", sb.toString());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Map<String, Object> getFreelancerProfile(@PathVariable Long id) {
        Freelancer freelancer = freelancerService.findById(id);
        List<Projects> projects = projectService.getAllProjectsByFreelancerId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("freelancer", freelancer);
        response.put("projects", projects);
        return response;
    }

    @GetMapping("client/viewprofile/{id}")
    public String viewClientProfile(@PathVariable("id") Long id, Model model) {
        System.out.println("Viewing profile for client ID: " + id);
        User user = userService.findById(id).orElse(null);
        String role = user.getRole();
        // if(!role.equals("CLIENT")){
        // System.out.println("User with ID: " + id + " is not a client, role found: " +
        // role);
        // return "error"; // or some error page
        // }

        Client client = clientServices.findById(user.getId());
        // if (client == null) {
        // System.out.println("Client not found for ID: " + id);
        // return "error"; // or some error page
        // }
        model.addAttribute("client", client);
        model.addAttribute("user", user);
        return "client/profile";
    }

    @GetMapping("/tasks/{id}")
    public String viewTasks(@PathVariable("id") Long projectId, Model model) {

        Projects project = projectService.getProjectById(projectId);
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        model.addAttribute("project", project);
        model.addAttribute("tasks", tasks);

        return "freelancer/taskslist";
    }

    @PostMapping("/task/generate/{projectId}")
    public ResponseEntity<?> generateTasks(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> reqBody) {

        String userDescription = reqBody.get("description");
        Projects project = projectService.getProjectById(projectId);

        if (project == null)
            return ResponseEntity.badRequest().body("Project not found");

        System.out.println("🚀 Generating tasks for project: " + project.getTitle());

        List<Map<String, Object>> aiTasks = geminiService.generateTaskTitles(
                project.getTitle(),
                project.getDescription(),
                userDescription);

        System.out.println("📋 Generated " + aiTasks.size() + " tasks from Gemini");

        List<Task> saved = new ArrayList<>();

        for (Map<String, Object> t : aiTasks) {
            Task task = new Task();
            task.setTitle((String) t.get("title"));
            task.setDescription((String) t.getOrDefault("description", ""));
            task.setCategory((String) t.getOrDefault("category", "General"));
            task.setTaskweightage(Long.valueOf(t.get("weightage").toString()));
            task.setTaskprogress(0L);
            task.setCompleted(false);
            task.setProject(project);
            task.setAssignedFreelancer(project.getFreelancer());

            Task savedTask = taskRepository.save(task);
            saved.add(savedTask);
            System.out.println("✅ Saved task: " + savedTask.getTitle() + " (ID: " + savedTask.getId() + ")");
        }

        System.out.println("✅ All " + saved.size() + " tasks saved to database");
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/api/tasks/list/{projectId}")
    public ResponseEntity<?> getTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskRepository.findByProjectId(projectId));
    }

    @PostMapping("/project/{projectId}/github")
    @ResponseBody
    public ResponseEntity<?> saveGithubRepository(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> requestBody) {

        String githubUrl = requestBody.get("githubUrl");

        if (githubUrl == null || githubUrl.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "GitHub URL is required");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Projects project = projectService.getProjectById(projectId);

            if (project == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Project not found");
                return ResponseEntity.badRequest().body(error);
            }

            project.setGithubUrl(githubUrl);
            projectService.create(project);

            Map<String, Object> success = new HashMap<>();
            success.put("success", true);
            success.put("message", "GitHub repository saved successfully");
            success.put("githubUrl", githubUrl);

            return ResponseEntity.ok(success);

        } catch (Exception e) {
            logger.error("Error saving GitHub URL for project " + projectId, e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error saving GitHub repository: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/edit-profile/{id}")
    public String editProfile(@PathVariable("id") Long id, Model model) {
        Freelancer freelancer = freelancerService.findById(id);
        if (freelancer == null) {
            logger.error("Freelancer with ID " + id + " not found.");
            return "error"; // return an error view or handle accordingly
        }
        model.addAttribute("freelancer", freelancer);
        return "freelancer/editfreelancer";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute Freelancer freelancer) {
        // Find freelancer by ID first, then fall back to email if needed
        Freelancer existingFreelancer = null;

        if (freelancer.getId() != null && freelancer.getId() > 0) {
            existingFreelancer = freelancerService.findById(freelancer.getId());
        } else if (freelancer.getEmail() != null) {
            existingFreelancer = freelancerService.findByEmail(freelancer.getEmail());
        }

        if (existingFreelancer == null) {
            logger.error("Freelancer with ID " + freelancer.getId() + " not found.");
            return "error"; // return an error view or handle accordingly
        }

        // Update fields
        existingFreelancer.setName(freelancer.getName());
        existingFreelancer.setPhone(freelancer.getPhone());
        existingFreelancer.setDesignation(freelancer.getDesignation());
        existingFreelancer.setBio(freelancer.getBio());

        // Add new skills to existing skills (merge instead of replace)
        if (freelancer.getSkills() != null && !freelancer.getSkills().isEmpty()) {
            List<String> existingSkills = existingFreelancer.getSkills();
            if (existingSkills == null) {
                existingSkills = new ArrayList<>();
            }

            // Add only new skills (avoid duplicates)
            for (String skill : freelancer.getSkills()) {
                if (!existingSkills.contains(skill)) {
                    existingSkills.add(skill);
                }
            }
            existingFreelancer.setSkills(existingSkills);
        }

        existingFreelancer.setImageUrl(freelancer.getImageUrl());

        freelancerService.save(existingFreelancer);
        return "redirect:/freelancer/dashboard/" + existingFreelancer.getId();

    }

    @PostMapping("/task/submit")
    public String submitTask(@RequestParam Long taskId,
            @RequestParam String message,
            @RequestParam(required = false) String githubLink,
            Model model) {

        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            // Handle task not found
            return "error"; // or some error page
        }
        Projects project = task.getProject();
        Freelancer freelancer = freelancerService.findById(project.getFreelancer().getId());
        Client client = project.getClient();

        WorkSubmission submission = submissionService.createSubmission(task, message, githubLink, freelancer);
        // Notify Client
        notificationService.notifyClient(
                client.getId(), submission.getId(),
                "Task Submitted for Review",
                "Freelancer submitted task: " + task.getTitle());

        // Save submission logic...

        return "redirect:/freelancer/tasks/" + project.getId();
    }

    @GetMapping("/notifications/{id}")
    public String freelancerNotifications(@PathVariable("id") Long id, Model model) {
        Freelancer freelancer = freelancerService.findById(id);
        if (freelancer == null) {
            return "redirect:/login"; // or some error page
        }
        List<Notification> notifications = notificationService.getfreelancerNotifications(freelancer.getId(),
                "FREELANCER");

        model.addAttribute("notifications", notifications);
        model.addAttribute("currentUser", freelancer);
        return "freelancer/notification";
    }

    @GetMapping("/notification/{notificationId}/delete")
    public String deleteNotification(@PathVariable Long notificationId, Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();
        if (loggedUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(loggedUser.getEmail());
        Freelancer freelancer = freelancerService.findById(user.getId());

        // Delete the notification
        notificationService.deleteNotification(notificationId);

        // Redirect back to notifications page
        return "redirect:/freelancer/notifications/" + freelancer.getId();
    }

    @PostMapping("/notifications/mark-read")
    public String markNotificationAsRead(@RequestParam Long notifId) {
        try {
            User loggedUser = commonMethodService.AuthenticateUser();
            if (loggedUser == null) {
                return "redirect:/login";
            }
            User user = userService.findByEmail(loggedUser.getEmail());
            Freelancer freelancer = freelancerService.findById(user.getId());

            if (freelancer == null) {
                return "redirect:/login";
            }

            // Mark notification as read
            notificationService.markAsRead(notifId);
            logger.info("✅ Notification " + notifId + " marked as read");

            // Redirect back to notifications page
            return "redirect:/freelancer/notifications/" + freelancer.getId();
        } catch (Exception e) {
            logger.error("❌ Error marking notification as read: " + e.getMessage());
            return "redirect:/freelancer/dashboard";
        }
    }

    // @GetMapping("/task/{taskId}/delete")
    // public String deleteTask(@PathVariable("taskId") Long taskId) {
    // Task task = taskRepository.findById(taskId).orElse(null);

    // if (task == null) {
    // logger.error("Task with ID " + taskId + " not found.");
    // return "redirect:/freelancer/dashboard/1"; // Redirect to dashboard if task
    // not found
    // }

    // Long projectId = task.getProject().getId();

    // // Delete the task
    // taskRepository.deleteById(taskId);
    // logger.info("Task with ID " + taskId + " has been deleted.");

    // // Redirect back to tasks list
    // return "redirect:/freelancer/tasks/" + projectId;
    // }

    @PostMapping("/api/task/{taskId}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteTaskApi(@PathVariable("taskId") Long taskId) {
        try {
            Task task = taskRepository.findById(taskId).orElse(null);

            if (task == null) {
                logger.error("Task with ID " + taskId + " not found.");
                return ResponseEntity.badRequest().body(Map.of("success", false, "error", "Task not found"));
            }

            taskRepository.deleteById(taskId);
            logger.info("✅ Task with ID " + taskId + " has been deleted via API.");

            return ResponseEntity.ok(Map.of("success", true, "message", "Task deleted successfully"));
        } catch (Exception e) {
            logger.error("❌ Error deleting task: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/api/task/create")
    @ResponseBody
    public ResponseEntity<?> createTask(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long projectId,
            Authentication authentication) {
        try {
            if (title == null || title.trim().isEmpty() || category == null || category.trim().isEmpty()) {
                logger.warn("⚠️ Task creation failed: Missing required fields");
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "Title and category are required"));
            }

            String email = authentication.getName();
            Freelancer freelancer = freelancerService.findByEmail(email);

            if (freelancer == null) {
                logger.error("❌ Freelancer not found for email: " + email);
                return ResponseEntity.status(401)
                        .body(Map.of("success", false, "error", "Freelancer not authenticated"));
            }

            Task task = new Task();
            task.setTitle(title);
            task.setCategory(category);
            task.setDescription(description != null ? description : "");
            task.setTaskprogress(0L);
            task.setCompleted(false);
            task.setAssignedFreelancer(freelancer);

            // Set project if projectId is provided
            if (projectId != null) {
                Projects project = projectService.getProjectById(projectId);
                if (project != null) {
                    task.setProject(project);
                    logger.info("✅ Task associated with project ID: " + projectId);
                }
            }

            Task savedTask = taskRepository.save(task);
            logger.info("✅ Task created successfully: " + savedTask.getTitle() + " (ID: " + savedTask.getId() + ")");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Task created successfully",
                    "taskId", savedTask.getId(),
                    "task", savedTask));
        } catch (Exception e) {
            logger.error("❌ Error creating task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/task/create")
    public String createTaskForm(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long projectId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            if (title == null || title.trim().isEmpty() || category == null || category.trim().isEmpty()) {
                logger.warn("⚠️ Task creation failed: Missing required fields");
                redirectAttributes.addFlashAttribute("error", "Title and category are required");
                return "redirect:/freelancer/task/create";
            }

            String email = authentication.getName();
            Freelancer freelancer = freelancerService.findByEmail(email);

            if (freelancer == null) {
                logger.error("❌ Freelancer not found for email: " + email);
                redirectAttributes.addFlashAttribute("error", "Freelancer not authenticated");
                return "redirect:/freelancer/dashboard";
            }

            Task task = new Task();
            task.setTitle(title);
            task.setCategory(category);
            task.setDescription(description != null ? description : "");
            task.setTaskprogress(0L);
            task.setCompleted(false);
            task.setAssignedFreelancer(freelancer);

            // Set project if projectId is provided
            if (projectId != null) {
                Projects project = projectService.getProjectById(projectId);
                if (project != null) {
                    task.setProject(project);
                    logger.info("✅ Task associated with project ID: " + projectId);
                }
            }

            Task savedTask = taskRepository.save(task);
            logger.info("✅ Task created successfully: " + savedTask.getTitle() + " (ID: " + savedTask.getId() + ")");

            redirectAttributes.addFlashAttribute("success", "Task created successfully!");
            return "redirect:/freelancer/tasks" + (projectId != null ? ("/" + projectId) : "");
        } catch (Exception e) {
            logger.error("❌ Error creating task: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to create task: " + e.getMessage());
            return "redirect:/freelancer/task/create";
        }
    }

    @GetMapping("/task/create")
    public String showCreateTaskForm(Model model, @RequestParam(required = false) Long projectId,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Freelancer freelancer = freelancerService.findByEmail(email);

            if (freelancer == null) {
                logger.error("❌ Freelancer not found for email: " + email);
                return "redirect:/freelancer/dashboard";
            }

            if (projectId != null) {
                Projects project = projectService.getProjectById(projectId);
                if (project != null) {
                    model.addAttribute("project", project);
                }
            }

            model.addAttribute("freelancer", freelancer);
            logger.info("✅ Loading create task page for: " + email);
            return "freelancer/createTask";
        } catch (Exception e) {
            logger.error("❌ Error loading create task page: " + e.getMessage());
            return "redirect:/freelancer/dashboard";
        }
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model, Authentication authentication) {
        try {
            String email = authentication.getName();
            Freelancer freelancer = freelancerService.findByEmail(email);

            if (freelancer == null) {
                logger.error("❌ Freelancer not found for email: " + email);
                return "redirect:/freelancer/dashboard";
            }

            model.addAttribute("freelancer", freelancer);
            logger.info("✅ Loading edit profile page for: " + email);
            return "freelancer/editProfile";
        } catch (Exception e) {
            logger.error("❌ Error loading edit profile: " + e.getMessage());
            return "redirect:/freelancer/dashboard";
        }
    }

    @PostMapping("/profile/update")
    public String updateFreelancerProfile(
            @ModelAttribute Freelancer updatedFreelancer,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Freelancer freelancer = freelancerService.findByEmail(email);

            if (freelancer == null) {
                logger.error("❌ Freelancer not found for email: " + email);
                redirectAttributes.addFlashAttribute("error", "Freelancer not found");
                return "redirect:/freelancer/dashboard";
            }

            // Update basic fields
            freelancer.setName(updatedFreelancer.getName());
            freelancer.setPhone(updatedFreelancer.getPhone());
            freelancer.setDesignation(updatedFreelancer.getDesignation());
            freelancer.setBio(updatedFreelancer.getBio());

            // Handle profile image upload
            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    // String imageUrl = imageServices.uploadImage(profileImage);
                    // freelancer.setImageUrl(imageUrl);
                    // logger.info("✅ Profile image uploaded: " + imageUrl);
                } catch (Exception e) {
                    logger.error("⚠️ Failed to upload image: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("warning", "Profile updated but image upload failed");
                }
            }

            // Save updated freelancer
            freelancerService.save(freelancer);
            logger.info("✅ Freelancer profile updated successfully for: " + email);

            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/freelancer/dashboard" + "/" + freelancer.getId();

        } catch (Exception e) {
            logger.error("❌ Error updating profile: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
            return "redirect:/freelancer/profile/edit";
        }
    }

    @GetMapping("/billing")
    public String showBillingAndPayments(Model model, Authentication authentication) {
        logger.info("✅ Loading billing and payments page");
        try {
            User authUser = commonMethodService.AuthenticateUser();
            if (authUser == null) {
                logger.warn("⚠️ Unauthenticated access to billing page");
                return "redirect:/login";
            }
            Freelancer freelancer = freelancerService.findByEmail(authUser.getEmail());

            if (freelancer == null) {
                logger.warn("⚠️ Freelancer not found: " + authUser.getEmail());
                return "redirect:/login";
            }

            // Calculate earnings summary
            List<Contract> completedContracts = contractService.getByFreelancerAndStatus(freelancer,
                    ContractStatus.COMPLETED);

            long totalEarnings = 0;
            if (completedContracts != null) {
                totalEarnings = completedContracts.stream()
                        .mapToLong(c -> c.getAmount() != null ? c.getAmount().longValue() : 0L)
                        .sum();
            }

            // Get pending payments (contracts not yet paid)
            long pendingPayments = 0;
            if (completedContracts != null) {
                pendingPayments = completedContracts.stream()
                        .filter(c -> c.getPaymentStatus() == null ||
                                !c.getPaymentStatus().equals("PAID"))
                        .mapToLong(c -> c.getAmount() != null ? c.getAmount().longValue() : 0L)
                        .sum();
            }

            // Count completed transactions
            int completedTransactions = 0;
            if (completedContracts != null) {
                completedTransactions = (int) completedContracts.stream()
                        .filter(c -> c.getPaymentStatus() != null && c.getPaymentStatus().equals("PAID"))
                        .count();
            }

            model.addAttribute("totalEarnings", totalEarnings);
            model.addAttribute("pendingPayments", pendingPayments);
            model.addAttribute("completedTransactions", completedTransactions);
            model.addAttribute("freelancer", freelancer);

            logger.info("✅ Billing page data loaded - Total: ₹" + totalEarnings + ", Pending: ₹" + pendingPayments);
            return "freelancer/billingPayments";
        } catch (Exception e) {
            logger.error("❌ Error loading billing page: " + e.getMessage(), e);
            return "redirect:/freelancer/dashboard";
        }
    }
}