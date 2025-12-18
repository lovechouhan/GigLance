package com.minor.freelancing.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.minor.freelancing.Entities.Contract;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Helper.ContractStatus;
import com.minor.freelancing.Services.ContractService;
import com.minor.freelancing.Services.ProjectService;
import com.minor.freelancing.Services.ProposalService;
import com.minor.freelancing.Services.UserService;

@Controller
@RequestMapping("/contracts")
public class ContractController {

    private final ContractService contractService;
    private final ProposalService proposalService;
    private final UserService userService;
    private final ProjectService projectService;

    public ContractController(ContractService contractService, ProposalService proposalService,
            UserService userService, ProjectService projectService) {
        this.contractService = contractService;
        this.proposalService = proposalService;
        this.userService = userService;
        this.projectService = projectService;
    }

    // @GetMapping("/client/{clientId}")
    // public String viewClientContracts(@PathVariable Long clientId, Model model) {
    // List<Contract> contracts = contractService.getContractsByClientId(clientId);
    // model.addAttribute("contracts", contracts);
    // return "Contracts/contract";
    // }

    // @GetMapping("/freelancer/{freelancerId}")
    // public String viewFreelancerContracts(@PathVariable Long freelancerId, Model
    // model) {
    // List<Contract> contracts =
    // contractService.getContractsByFreelancerId(freelancerId);
    // model.addAttribute("contracts", contracts);
    // return "Contracts/contract";
    // }

    @GetMapping("/activeContracts/{id}")
    public String viewActiveContracts(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return "error/404"; // or some error page
        }
        String role = user.getRole();
        if (role.equals("ROLE_CLIENT")) {
            List<Contract> contracts = contractService.getContractsByClientId(id);
            // Only get projects that have contracts
            List<Projects> projects = contracts.stream()
                    .map(Contract::getProject)
                    .distinct()
                    .toList();

            if (projects == null || projects.isEmpty()) {
                System.out.println("No projects with contracts found for client with ID: " + id);
            }
            model.addAttribute("projects", projects);
            model.addAttribute("contracts", contracts);
        } else if (role.equals("ROLE_FREELANCER")) {
            List<Contract> contracts = contractService.getContractsByFreelancerId(id);
            // Only get projects that have contracts
            List<Projects> projects = contracts.stream()
                    .map(Contract::getProject)
                    .distinct()
                    .toList();

            if (projects == null || projects.isEmpty()) {
                System.out.println("No projects with contracts found for freelancer with ID: " + id);
            }
            model.addAttribute("projects", projects);
            model.addAttribute("contracts", contracts);
        } else {
            return "redirect:/error/403"; // or some error page
        }
        return "Contracts/contracts";
    }

    @GetMapping("/viewContractDetails/{projectId}")
    public String viewContractDetails(@PathVariable Long projectId, Model model) {
        Projects project = projectService.findById(projectId);
        if (project == null) {
            System.out.println("Project not found for ID: " + projectId);
            return "error/404"; // or some error page
        }
        Contract contract = contractService.getContractByProjectId(projectId);
        if (contract == null) {
            System.out.println("No contract found for project ID: " + projectId);
            // Check if a freelancer is assigned to the project
            if (project.getFreelancer() != null) {
                System.out.println("Freelancer assigned to project. Creating contract...");
                // Create a new contract for this project
                contract = new Contract();
                contract.setProject(project);
                contract.setClient(project.getClient());
                contract.setFreelancer(project.getFreelancer());
                contract.setAmount(project.getBudget());
                contract.setStatus(ContractStatus.NOT_STARTED);
                contract.setCreatedAt(LocalDateTime.now());
                contractService.save(contract);
                System.out.println("Contract created successfully for project ID: " + projectId);
            } else {
                System.out.println("No freelancer assigned to project ID: " + projectId);
                model.addAttribute("project", project);
                model.addAttribute("contract", null);
                return "Contracts/contractDetail";
            }
        }
        model.addAttribute("contract", contract);
        model.addAttribute("project", project);
        return "Contracts/contractDetail";
    }

    @GetMapping("/receipt/{id}")
    public String contractReceipt(@PathVariable("id") Long id, Model model) {
        Contract contract = contractService.findById(id).orElse(null);
        if (contract == null) {
            return "redirect:/error/404"; // or some error page
        }
        model.addAttribute("contract", contract);
        return "Contracts/contractrecipiet"; // new Thymeleaf template to render receipt
    }

    @GetMapping("/{id}/sign")
    public String showSignPage(@PathVariable Long id,
            @RequestParam String role,
            @RequestParam String token,
            Model model) {

        Contract c = contractService.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        boolean valid = false;

        if (role.equals("client") && token.equals(c.getClientSignToken()))
            valid = true;
        if (role.equals("freelancer") && token.equals(c.getFreelancerSignToken()))
            valid = true;

        if (!valid) {
            model.addAttribute("error", "Invalid or expired link");
            return "sign-page";
        }

        model.addAttribute("contract", c);
        model.addAttribute("role", role);
        model.addAttribute("token", token);

        return "Contracts/sign-page";
    }

    // for saving signature
    @PostMapping("/{id}/sign")
    @ResponseBody
    public Map<String, Object> saveSignature(@PathVariable Long id,
            @RequestParam String role,
            @RequestParam String token,
            @RequestBody Map<String, String> body) {

        String base64 = body.get("image");

        // remove prefix
        if (base64.startsWith("data:")) {
            base64 = base64.substring(base64.indexOf(",") + 1);
        }

        Contract c = contractService.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (role.equals("client") && token.equals(c.getClientSignToken())) {
            c.setClientSignature(base64);
            c.setClientSignedAt(LocalDateTime.now());
            c.setStatus(ContractStatus.PENDING_FREELANCER_SIGNATURE);
        }

        else if (role.equals("freelancer") && token.equals(c.getFreelancerSignToken())) {
            c.setFreelancerSignature(base64);
            c.setFreelancerSignedAt(LocalDateTime.now());
            c.setStatus(ContractStatus.COMPLETED);
        }

        else
            return Map.of("success", false, "message", "Invalid token");

        contractService.save(c);

        return Map.of("success", true);
    }

    @PostMapping("/saveSignature/{id}")
    @ResponseBody
    public Map<String, Object> saveSignature(@PathVariable Long id,
            @RequestParam String role,
            @RequestBody Map<String, String> body) {
        try {
            String base64 = body.get("image");
            if (base64 == null || base64.isEmpty()) {
                return Map.of("success", false, "error", "Image data is empty");
            }

            base64 = base64.substring(base64.indexOf(",") + 1); // remove prefix

            // Validate signature is not blank (minimum size for a meaningful signature)
            if (base64.length() < 100) {
                return Map.of("success", false, "error",
                        "Signature is blank or too small. Please draw your signature.");
            }
            System.out.println("Saving " + role + " signature for contract " + id);

            Contract c = contractService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contract not found with id: " + id));

            if (role.equals("client")) {
                c.setClientSignature(base64);
                c.setClientSignedAt(LocalDateTime.now());
                System.out.println("Client signature saved");
            } else if (role.equals("freelancer")) {
                c.setFreelancerSignature(base64);
                c.setFreelancerSignedAt(LocalDateTime.now());
                System.out.println("Freelancer signature saved");
            } else {
                return Map.of("success", false, "error", "Invalid role: " + role);
            }

            contractService.save(c);
            System.out.println("Contract saved successfully");
            return Map.of("success", true);
        } catch (Exception e) {
            System.err.println("Error saving signature: " + e.getMessage());
            e.printStackTrace();
            return Map.of("success", false, "error", e.getMessage());
        }
    }

}