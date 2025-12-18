package com.minor.freelancing.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.minor.freelancing.CommonMethod.CommonMethodService;
import com.minor.freelancing.DTO.ProjectDto;
import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Escrow;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Notification;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Entities.Task;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Entities.WorkSubmission;
import com.minor.freelancing.Services.ClientServices;
import com.minor.freelancing.Services.EscrowServices;
import com.minor.freelancing.Services.FreelancerServices;
import com.minor.freelancing.Services.NotificationService;
import com.minor.freelancing.Services.ProjectService;
import com.minor.freelancing.Services.ProposalService;
import com.minor.freelancing.Services.UserService;
import com.minor.freelancing.Services.WorkSubmissionService;

@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private FreelancerServices freelancerService;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ClientServices clientServices;

    @Autowired
    private EscrowServices escrowServices;

    @Autowired
    private CommonMethodService commonMethodService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WorkSubmissionService submissionService;

    @GetMapping("/dashboard/{id}")
    public String clientDashboard(@PathVariable("id") Long id, Model model) {

        Client client = clientServices.findById(id);
        if (client == null) {
            return "redirect:/login"; // or some error page
        }

        try {
            String AmountSpent = clientServices.getAmountSpentByClientId(id);
            Long activeProjects = clientServices.getAllActiveProjectsCountByClientId(id);
            int completedProjects = clientServices.getCompletedProjects();
            Long proposalsReceived = clientServices.getProposalsReceivedbyClientId(id);
            int proposalsAccepted = clientServices.getProposalsAccepted();
            List<Projects> activeProjectsList = clientServices.getAllActiveProjectsByClientId(id);
            Long TotalProjects = (long) clientServices.getAllProjectsByClientId(id).size();
            Long invitationsSend = clientServices.getInvitationsSentByClientId(id);
            List<String> months = List.of("Jan", "Feb", "Mar", "Apr", "May");
            List<Integer> spending = List.of(9000, 7000, 6500, 9000, 12000);

            model.addAttribute("months", months);
            model.addAttribute("spending", spending);

            model.addAttribute("client", client);
            model.addAttribute("AmountSpent", AmountSpent);
            model.addAttribute("activeProjects", activeProjects);
            model.addAttribute("completedProjects", completedProjects);
            model.addAttribute("activeProjectsList", activeProjectsList);
            model.addAttribute("TotalProjects", TotalProjects);
            model.addAttribute("proposalsReceived", proposalsReceived);
            model.addAttribute("proposalsAccepted", proposalsAccepted);
            model.addAttribute("invitationsSend", invitationsSend);

            // model.addAttribute("user", user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "client/dashboard";
    }

    @GetMapping("/findfreelancers")
    public String findFreelancers(Model model) {
        List<Freelancer> freelancers = clientServices.getAllFreelancers();
        model.addAttribute("freelancers", freelancers);
        return "client/findfreelancers";
    }

    @GetMapping("/freelancer/viewprofile/{id}")
    public String viewFreelancerProfile(@PathVariable("id") Long id, Model model) {
        Freelancer freelancer = freelancerService.findById(id);
        if (freelancer == null) {
            System.out.println("Freelancer not found for ID: " + id);
            return "error"; // or some error page
        }
        model.addAttribute("freelancer", freelancer);
        return "freelancer/ViewFreelancerProfile";
    }

    @GetMapping("/myprojects")
    public String checkout(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email;

        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            // For OAuth2 (Google) login
            OAuth2User oauth2User = oauthToken.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            // For regular login
            email = auth.getName();
        }
        Client client = clientServices.findByEmail(email);
        if (client == null) {
            return null;
        }

        Long clientId = client.getId(); // Get the client ID from the client object
        List<Projects> projects = clientServices.getAllProjectsByClientId(clientId);
        if (projects == null) {
            System.out.println("No projects found for client ID: " + clientId);
            return "client/myprojects";
        }
        model.addAttribute("projects", projects);
        return "client/myprojects";
    }

    @GetMapping("/payment/{id}")
    public String payment(@PathVariable("id") Long id, Model model) {
        // You can add logic here to handle payment for the client with the given id
        model.addAttribute("clientId", id);
        List<Escrow> payments = escrowServices.getEscrowsByClientId(id);
        model.addAttribute("payments", payments);
        return "client/Payments";
    }

    @GetMapping("/payment/receipt/{id}")
    public String paymentReceipt(@PathVariable("id") Long id, Model model) {
        Escrow escrow = escrowServices.findById(id);
        if (escrow == null) {
            // fallback: redirect back to payments list or show a simple error page
            return "redirect:/client/payment/"
                    + (escrow != null && escrow.getClient() != null ? escrow.getClient().getId() : "");
        }

        model.addAttribute("escrow", escrow);
        return "client/receipt"; // new Thymeleaf template to render receipt
    }

    @GetMapping("/postproject")
    public String postProject(Model model) {
        ProjectDto projectDto = new ProjectDto();
        model.addAttribute("projectDto", projectDto);
        return "client/postproject";
    }

    @PostMapping("/postproject")
    public String postProject2(@ModelAttribute("projectDto") ProjectDto projectDto) {

        clientServices.postProject(projectDto);
        return "redirect:/client/myprojects";

    }

    @GetMapping("/profile")
    public String profile() {
        return "client/profile";
    }

    @GetMapping("/viewproposal/{id}") // user ki id le rahe hain yaha
    public String getViewProposal(@PathVariable("id") Long id, Model model) {
        Client client = clientServices.findById(id);

        List<Projects> projects = clientServices.getAllProjectsByClientId(id);
        model.addAttribute("projects", projects);
        return "client/viewproposal2";
    }

    @GetMapping("/viewproposalofanproject/{id}")
    public String getProposalsForProject(@PathVariable Long id, Model model) {
        List<Proposal> proposals = proposalService.getAllProposalsByProjectId(id);
        if (proposals == null || proposals.isEmpty()) {
            System.out.println("No proposals found for project ID: " + id);
        } else {
            System.out.println("Proposals found: " + proposals.size());
        }

        // Long freelancerId = null;
        // for (Proposal p : proposals) {
        // if (p.getFreelancerId() != null) {
        // freelancerId = p.getFreelancerId();
        // System.out.println("Proposal " + p.getId() + " has freelancer ID: " +
        // freelancerId);
        // } else {
        // System.out.println("Proposal " + p.getId() + " has no freelancer assigned.");
        // }
        // }
        // Assuming each proposal has projectId, fetch project too
        Projects project = projectService.findById(id);

        Freelancer freelancer = project.getFreelancer();
        if (freelancer == null) {
            System.out.println("No freelancer found for project ID: " + id);
        } else {
            System.out.println("Freelancer found: " + freelancer.getName());
        }

        model.addAttribute("proposals", proposals);
        model.addAttribute("project", project);
        model.addAttribute("freelancer", freelancer);

        return "client/viewproposal";
    }

    @GetMapping("activeprojects")
    public String getActiveProjects() {
        return "client/activeprojects";
    }

    @GetMapping("/projects/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id,
            org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        try {
            projectService.deleteById(id);
            ra.addFlashAttribute("successMessage", "Project deleted successfully.");
        } catch (Exception ex) {
            // handle and show friendly message
            System.err.println("Failed to delete project id=" + id + ": " + ex.getMessage());
            ra.addFlashAttribute("errorMessage", "Unable to delete project. It may have dependent records.");
        }
        return "redirect:/client/myprojects";
    }

    @GetMapping("/projects/viewdetails/{id}")
    public String viewProjectDetails(@PathVariable("id") Long id, Model model) {
        Projects project = projectService.findById(id);
        if (project == null) {
            System.out.println("Project not found for ID: " + id);
            return "error";
        }
        model.addAttribute("project", project);
        return "client/projectDetails";
    }

    @GetMapping("/project/{id}/approvals")
    public String editProjectForm(@PathVariable("id") Long id, Model model) {
        Projects project = projectService.findById(id);
        if (project == null) {
            System.out.println("Project not found for ID: " + id);
            return "error";
        }
        model.addAttribute("project", project);
        return "client/projectApprovals";
    }

    @GetMapping("/notifications/{id}")
    public String clientNotifications(@PathVariable("id") Long id, Model model) {

        Client client = clientServices.findById(id);

        List<Notification> notifications = notificationService.getClientNotifications(client.getId());
        long unread = notificationService.getClientUnreadCount(client.getId());

        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unread);
        model.addAttribute("clientId", id);
        // model.addAttribute("clientNotif",
        // notificationService.getClientUnreadCount(client.getId()));

        return "client/clientNotifications";
    }

    @PostMapping("/notifications/mark-read")
    public String markRead(@RequestParam Long notifId, @RequestParam Long clientId) {
        notificationService.markAsRead(notifId);
        return "redirect:/client/notifications/" + clientId;
    }

    @GetMapping("/review/{submissionId}")
    public String reviewSubmission(@PathVariable Long submissionId, @RequestParam(required = false) Long notificationId,
            Model model) {
        System.out.println("Reviewing submission ID: " + submissionId + " with notification ID: " + notificationId);
        Notification notify = notificationService.getNotificationById(notificationId);
        WorkSubmission submission = submissionService.getSubmittedWork(notify.getRelatedSubmissionId());

        // Handle null submission
        if (submission == null) {
            System.err.println("Error: WorkSubmission with ID " + submissionId + " not found");
        }

        model.addAttribute("submission", submission);
        model.addAttribute("notificationId", notificationId);

        return "client/reviewSubmission";
    }

    @GetMapping("/notification/{notificationId}/delete")
    public String deleteNotification(@PathVariable Long notificationId, Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();
        if (loggedUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(loggedUser.getEmail());
        Client client = clientServices.findById(user.getId());

        // Delete the notification
        notificationService.deleteNotification(notificationId);

        // Redirect back to notifications page
        return "redirect:/client/notifications/" + client.getId();
    }

    @GetMapping("/submission/approve/{submissionId}")
    public String approveSubmission(@PathVariable Long submissionId,
            @RequestParam(required = false) Long notificationId) {
        WorkSubmission submission = submissionService.getSubmittedWork(submissionId);
        if (submission == null || submission.getTask() == null || submission.getTask().getProject() == null) {
            return "redirect:/client/notifications"; // nothing to approve
        }

        Task task = submission.getTask();
        Long weightage = task.getTaskweightage() != null ? task.getTaskweightage() : 0L;
        Projects project = task.getProject();
        Long progress = project.getProgress() != null ? project.getProgress() : 0L;
        project.setProgress(progress + weightage);
        submission.setApproved(true);
        submission.setRejected(false);
        submissionService.updateSubmission(submission);

        // Notify freelancer about approval
        notificationService.notifyFreelancer(
                submission.getFreelancer().getId(),
                "Work Submission Approved",
                "Your submission for task '" + submission.getTask().getTitle() + "' has been approved!");

        // Delete the notification if provided
        if (notificationId != null) {
            notificationService.deleteNotification(notificationId);
        }

        User loggedUser = commonMethodService.AuthenticateUser();
        if (loggedUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(loggedUser.getEmail());
        Client client = clientServices.findById(user.getId());

        return "redirect:/client/notifications/" + client.getId();
    }

    @GetMapping("/submission/reject/{submissionId}")
    public String rejectSubmission(@PathVariable Long submissionId,
            @RequestParam(required = false) Long notificationId) {
        WorkSubmission submission = submissionService.getSubmittedWork(submissionId);
        if (submission != null) {
            submission.setRejected(true);
            submission.setApproved(false);
            submissionService.updateSubmission(submission);

            // Notify freelancer about rejection
            notificationService.notifyFreelancer(
                    submission.getFreelancer().getId(),
                    "Work Submission Rejected",
                    "Your submission for task '" + submission.getTask().getTitle()
                            + "' was rejected. Please review and resubmit.");
        }

        // Delete the notification if provided
        if (notificationId != null) {
            notificationService.deleteNotification(notificationId);
        }

        User loggedUser = commonMethodService.AuthenticateUser();
        if (loggedUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(loggedUser.getEmail());
        Client client = clientServices.findById(user.getId());

        return "redirect:/client/notifications/" + client.getId();
    }

    @GetMapping("/profile/{id}")
    public String viewClientProfile(@PathVariable("id") Long id, Model model) {
        Client client = clientServices.findById(id);
        if (client == null) {
            return "error/404";
        }
        List<Projects> recentProjects = clientServices.getAllProjectsByClientId(id);
        if (recentProjects.size() > 3) {
            recentProjects = recentProjects.subList(0, 3);
        }
        model.addAttribute("client", client);
        model.addAttribute("recentProjects", recentProjects);
        return "client/profile";
    }

    @GetMapping("/edit/{id}")
    public String editClientProfile(@PathVariable("id") Long id, Model model) {
        Client client = clientServices.findById(id);
        if (client == null) {
            return "error/404";
        }
        model.addAttribute("client", client);
        return "client/editProfile";
    }

    @PostMapping("/profile/save/{id}")
    public String saveClientProfile(@PathVariable("id") Long id,
            @ModelAttribute Client updatedClient,
            @RequestParam(value = "profileImage", required = false) org.springframework.web.multipart.MultipartFile profileImage,
            Model model) {
        try {
            Client client = clientServices.findById(id);
            if (client == null) {
                return "error/404";
            }
            clientServices.updateClientProfile(id, updatedClient, profileImage);
            return "redirect:/client/profile/" + id;
        } catch (Exception e) {
            System.out.println("Error saving profile: " + e.getMessage());
            model.addAttribute("error", "Failed to save profile");
            model.addAttribute("client", clientServices.findById(id));
            return "client/editProfile";
        }
    }

    @GetMapping("/allpayments/{id}")
    public String allPayments(@PathVariable("id") Long id, Model model) {
        // You can add logic here to handle payment for the client with the given id
        model.addAttribute("clientId", id);
        List<Escrow> payments = escrowServices.getEscrowsByClientId(id);
        model.addAttribute("payments", payments);
        return "client/PaymentsEnhanced";
    }

}
