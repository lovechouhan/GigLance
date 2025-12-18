package com.minor.freelancing.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.minor.freelancing.DTO.ProposalDto;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Services.ContractService;
import com.minor.freelancing.Services.FreelancerServices;
import com.minor.freelancing.Services.InvitationServices;
import com.minor.freelancing.Services.NotificationService;
import com.minor.freelancing.Services.ProjectService;
import com.minor.freelancing.Services.ProposalService;
import com.minor.freelancing.Services.UserService;


@Controller
@RequestMapping("/proposals")
public class ProposalController {

    private final ProposalService proposalService;
    private final FreelancerServices freelancerService;
    private final ProjectService projectService;
    private final UserService userService;
    private final InvitationServices invitationService;
    private final NotificationService notificationService;

    private final com.minor.freelancing.Services.ContractService contractService;

    public ProposalController(ProposalService proposalService, ProjectService projectService, UserService userService,
            ContractService contractService, FreelancerServices freelancerService,
            InvitationServices invitationService, NotificationService notificationService) {
        this.proposalService = proposalService;
        this.projectService = projectService;
        this.userService = userService;
        this.contractService = contractService;
        this.freelancerService = freelancerService;
        this.invitationService = invitationService;
        this.notificationService = notificationService;
    }

    @PostMapping("/sendProposals/{id}")
public String submitProposal(
        @ModelAttribute ProposalDto proposalDto,
        @RequestParam("coverLetter") MultipartFile coverLetter,
        @PathVariable Long id,
        RedirectAttributes redirectAttrs
) {
    try {
        proposalDto.setCoverLetter(coverLetter);
        proposalService.create(proposalDto, id);
        
       
        redirectAttrs.addFlashAttribute("success",
                "Proposal submitted successfully!");
        return "redirect:/freelancer/dashboard/" + id;

    } catch (Exception e) {
        e.printStackTrace();   // DEBUG ke liye zaroori
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        return "redirect:/freelancer/proposalform";
    }
}


    @GetMapping("/viewproposals")
    public String viewProposals(Model model) {
        List<Proposal> proposals = proposalService.getAllProposalsDoneByFreelancer();

        // Map Proposal entities to ProposalDto enriched with project/client display
        // fields
        java.util.List<ProposalDto> dtoList = new java.util.ArrayList<>();
        for (Proposal p : proposals) {
            ProposalDto dto = ProposalDto.builder()
                    .id(p.getId())
                    .project(p.getProject())
                    .freelancer(p.getFreelancer())
                    .bidAmount(p.getBidAmount())
                    .timelineDays(p.getTimelineDays())
                    .coverLetterUrl(p.getCoverLetterUrl())
                    .coverLetterFileName(p.getCoverLetterFileName())
                    .coverLetterFileType(p.getCoverLetterFileType())
                    .status(p.getStatus())
                    .createdAt(p.getCreatedAt())
                    .build();

            // project title (if available)
            try {
               Projects proj = projectService.findById(p.getProject().getId());
                if (proj != null) {
                    dto.setProjectTitle(proj.getTitle());
                    dto.setTitle(proj.getTitle());
                }
            } catch (Exception ex) {
                // ignore - leave projectTitle null
            }

            // client name (if available)
            try {
                userService.findById(p.getClient().getId()).ifPresent(u -> dto.setClientName(u.getName()));
            } catch (Exception ex) {
                // ignore
            }

            // status icon mapping
            String icon = "";
            if (p.getStatus() != null) {
                switch (p.getStatus()) {
                    case PENDING:
                        icon = "⏳";
                        break;
                    case ACCEPTED:
                        icon = "✅";
                        break;
                    case REJECTED:
                        icon = "❌";
                        break;
                    default:
                        icon = "";
                }
            }
            dto.setStatusIcon(icon);

            dtoList.add(dto);
        }

        model.addAttribute("proposals", dtoList);
        return "freelancer/proposal"; // Return the view name to display proposals
    }

    @GetMapping("/accept/{id}")
    public String acceptProposal(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        proposalService.acceptProposal(id);
        redirectAttrs.addFlashAttribute("successMessage", "Proposal accepted successfully!");
        // After accepting, redirect back to the project's proposal listing so client
        // sees updated status
        Proposal prop = proposalService.findById(id).orElse(null);
        if (prop != null && prop.getProject() != null) {
            return "redirect:/client/viewproposalofanproject/" + prop.getProject();
        }

        return "redirect:/client/myprojects"; // fallback
    }

    @GetMapping("/reject/{id}")
    public String rejectProposal(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        proposalService.rejectProposal(id);
        redirectAttrs.addFlashAttribute("successMessage", "Proposal rejected successfully!");
        Proposal prop = proposalService.findById(id).orElse(null);
        if (prop != null && prop.getProject() != null) {
            return "redirect:/client/viewproposalofanproject/" + prop.getProject().getId();
        }
        return "redirect:/client/myprojects"; // fallback
    }

    @GetMapping("/downloadCoverLetter/{id}")
    public String downloadCoverLetter(@PathVariable Long id, Model model) {
        Proposal proposal = proposalService.findById(id).orElse(null);
        String URL = proposal.getCoverLetterUrl();
        model.addAttribute("proposal", proposal);
        return "redirect:" + URL;
    }


}
