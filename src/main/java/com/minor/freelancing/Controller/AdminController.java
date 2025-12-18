package com.minor.freelancing.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.minor.freelancing.CommonMethod.CommonMethodService;
import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Contract;
import com.minor.freelancing.Entities.Escrow;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Helper.PaymentStatus;
import com.minor.freelancing.Services.ClientServices;
import com.minor.freelancing.Services.ContractService;
import com.minor.freelancing.Services.EscrowServices;
import com.minor.freelancing.Services.FreelancerServices;
import com.minor.freelancing.Services.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CommonMethodService commonMethodService;

    @Autowired
    private UserService userService;

    @Autowired
    private EscrowServices escrowServices;

    @Autowired
    private ContractService contractService;

    @Autowired
    private ClientServices clientService;

    @Autowired
    private FreelancerServices freelancerServices;

    // Check if user is admin
    private boolean isAdmin(User user) {
        return user != null && user.getRole() != null && user.getRole().equals("ROLE_ADMIN");
    }

    // Redirect to login if not authenticated
    private String redirectIfNotAuthenticated(User user) {
        if (user == null) {
            return "redirect:/login";
        }
        return null;
    }

    // Redirect to unauthorized if not admin
    private String redirectIfNotAdmin(User user) {
        if (!isAdmin(user)) {
            return "redirect:/unauthorized";
        }
        return null;
    }

    /**
     * Admin Dashboard - Overview of payments, contracts, and system statistics
     */
    @GetMapping("/dashboard/{id}")
    public String adminDashboard(@PathVariable Long id, Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        User user = userService.findByEmail(loggedUser.getEmail());

        // Get payment statistics
        List<Escrow> allEscrows = escrowServices.getAllEscrows();
        List<Contract> allContracts = contractService.findAll();

        long totalPayments = 0;
        long pendingPayments = 0;
        long completedPayments = 0;

        if (allEscrows != null) {
            for (Escrow escrow : allEscrows) {
                if (escrow.getAmount() != null) {
                    totalPayments += escrow.getAmount().longValue();
                }
            }
            pendingPayments = allEscrows.stream()
                    .filter(e -> e.getStatus() == null || !e.getStatus().equals("COMPLETED"))
                    .count();
            completedPayments = allEscrows.stream()
                    .filter(e -> e.getStatus() != null && e.getStatus().equals("COMPLETED"))
                    .count();
        }

        model.addAttribute("totalPayments", totalPayments);
        model.addAttribute("totalEscrows", allEscrows != null ? allEscrows.size() : 0);
        model.addAttribute("pendingPayments", pendingPayments);
        model.addAttribute("completedPayments", completedPayments);
        model.addAttribute("totalContracts", allContracts != null ? allContracts.size() : 0);
        model.addAttribute("user", user);

        return "admin/dashboard";
    }

    /**
     * View all escrow/payment records
     */
    @GetMapping("/payments")
    public String viewAllPayments(Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        User user = userService.findByEmail(loggedUser.getEmail());
        List<Escrow> payments = escrowServices.getAllEscrows();
        model.addAttribute("payments", payments); // List<Escrow>
        model.addAttribute("statuses", PaymentStatus.values());

        model.addAttribute("user", user);

        return "admin/payment";
    }

    /**
     * View payment details
     */
    @GetMapping("/payment/{id}")
    public String viewPaymentDetails(@PathVariable Long id, Model model) {
        User loggedUser = userService.findById(id).orElse(null);

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        User user = userService.findByEmail(loggedUser.getEmail());
        List<Escrow> escrow = escrowServices.getAllEscrows();

        if (escrow == null) {
            return "redirect:/admin/payments";
        }

        model.addAttribute("escrow", escrow);
        model.addAttribute("user", user);

        return "admin/payment-details";
    }

    /**
     * Update payment status
     */
    @GetMapping("/allclients")
    public String viewAllClients(Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        User user = userService.findByEmail(loggedUser.getEmail());
        List<User> clients = userService.getAllClients();

        model.addAttribute("clients", clients);
        model.addAttribute("user", user);

        return "admin/all-clients";
    }

    @GetMapping("/allfreelancers")
    public String viewAllFreelancers(Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        User user = userService.findByEmail(loggedUser.getEmail());
        List<User> freelancers = userService.getAllFreelancers();

        model.addAttribute("freelancers", freelancers);
        model.addAttribute("user", user);

        return "admin/all-freelancers";
    }

    @GetMapping("/clientprofile/{id}")
    public String viewClientProfile(@PathVariable Long id, Model model) {
       

        

        User user = userService.findById(id).orElse(null);
        Client client = clientService.findById(id);

        model.addAttribute("client", client);
        model.addAttribute("user", user);
        // model.addAttribute("loggedInUser", loggedUser);

        return "admin/viewClientProfile";
    }

    @GetMapping("/freelancerprofile/{id}")
    public String viewFreelancerProfile(@PathVariable Long id, Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        Freelancer freelancer = freelancerServices.findById(id);
        User user = userService.findByEmail(loggedUser.getEmail());

        model.addAttribute("freelancer", freelancer);
        model.addAttribute("user", user);
        model.addAttribute("loggedInUser", loggedUser);

        return "admin/viewFreelancerProfile";
    }

    /**
     * View all contracts
     */
    @GetMapping("/contracts")
    public String viewAllContracts(Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        User user = userService.findByEmail(loggedUser.getEmail());
        List<Contract> contracts = contractService.findAll();

        model.addAttribute("contracts", contracts);
        model.addAttribute("user", user);

        return "admin/contracts";
    }

    /**
     * View contract details
     */
    @GetMapping("/contract/{id}")
    public String viewContractDetails(@PathVariable Long id, Model model) {
        User loggedUser = commonMethodService.AuthenticateUser();

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        User user = userService.findByEmail(loggedUser.getEmail());
        Contract contract = contractService.findById(id).orElse(null);

        if (contract == null) {
            return "redirect:/admin/contracts";
        }

        model.addAttribute("contract", contract);
        model.addAttribute("user", user);

        return "admin/contract-details";
    }

    /**
     * Release payment to freelancer
     */
    @PostMapping("/payment/{id}/release")
    public String releasePayment(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks) {

        User loggedUser = commonMethodService.AuthenticateUser();

        String authCheck = redirectIfNotAuthenticated(loggedUser);
        if (authCheck != null)
            return authCheck;

        String adminCheck = redirectIfNotAdmin(loggedUser);
        if (adminCheck != null)
            return adminCheck;

        Escrow escrow = escrowServices.findById(id);
        if (escrow != null) {
            // Update payment status to COMPLETED
            escrow.setStatus(PaymentStatus.COMPLETED);
            // You can add a timestamp or remarks field to track when payment was released
            // escrow.setReleasedAt(LocalDateTime.now());
            // escrow.setAdminRemarks(remarks);

            // Save the updated escrow
            escrowServices.updateEscrow(escrow);
        }

        return "redirect:/admin/payments";
    }
}
