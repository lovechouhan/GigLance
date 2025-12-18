package com.minor.freelancing.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.minor.freelancing.DTO.InvitationDTO;
import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Invitations;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Services.ClientServices;
import com.minor.freelancing.Services.FreelancerServices;
import com.minor.freelancing.Services.InvitationServices;
import com.minor.freelancing.Services.ProjectService;
import com.minor.freelancing.Services.ContractService;

@Controller
@RequestMapping("/invite")
public class InvitationController {

    @Autowired
    private ClientServices clientService;

    @Autowired
    private FreelancerServices freelancerService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private InvitationServices invitationService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/client/{id}/{freelancerId}")
    public String inviteFreelancer(@PathVariable("id") Long id, @PathVariable("freelancerId") Long freelancerId,
            Model model) {
        try {
            Client client = clientService.findById(id);
            Freelancer freelancer = freelancerService.findById(freelancerId);
            if (client == null || freelancer == null) {
                System.out.println("User not found");
                return "error";
            }
            Invitations invitation = invitationService.sendInvitationToFreelancer(client, freelancer);
            InvitationDTO invitationDto = new InvitationDTO();
            invitationDto.setId(invitation.getId());
            invitationDto.setClientName(client);
            invitationDto.setFreelancerName(freelancer);

            List<Projects> projects = clientService.getAllProjectsByClientIdandStatus(id);
            model.addAttribute("invitationDto", invitationDto);
            model.addAttribute("freelancer", freelancer);
            model.addAttribute("client", client);
            model.addAttribute("projects", projects);
            return "client/sendInviteForm";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @PostMapping("/sendInvitation")
    public String sendInvitation(InvitationDTO invitationDto, Model model) {
        try {
            invitationService.saveInvitation(invitationDto);
            model.addAttribute("successMessage", "Invitation sent successfully!");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            model.addAttribute("errorMessage", "An unexpected error occurred while saving the invitation.");
        }

        // Repopulate model attributes required by the form template so Thymeleaf
        // bindings work

        return "client/sendInviteForm";
    }

    @GetMapping("/client/{id}/{userid}/accept")
    public String acceptClientInvitation(@PathVariable("id") Long id, @PathVariable("userid") Long userId,
            Model model) {

        Freelancer freelancer = freelancerService.findById(userId);
        if (freelancer == null) {
            System.out.println("Freelancer not found with id: " + userId);
            return "error";
        }
        Invitations invitation = invitationService.findById(id);
        if (invitation == null) {
            System.out.println("Invitation not found for ID: " + id);
            return "error";
        }

        // update invitation status to accepted
        invitation.setStatus("ACCEPTED");
        invitationService.save(invitation);
        contractService.createContractFromInvitation(invitation);

        Projects project = invitation.getProjectTitle();
        if (project == null) {
            System.out.println("Project not found for  ID: " + id);
            return "error";
        }
        Projects existingProject = projectService.getProjectById(project.getId());
        System.out.println("Existing Project: " + existingProject);
        if (existingProject == null) {
            System.out.println("Project not found for Invitation ID: " + id);
            return "error";
        }
        projectService.markProjectAsAssigned(existingProject, freelancer);

        return "redirect:/freelancer/invitations/" + freelancer.getId();
    }

    @GetMapping("/viewdetails/{id}") // inviteation id
    public String viewInvitationsForFreelancer(@PathVariable("id") Long id, Model model) {
        System.out.println("Viewing invitation details for ID: " + id);
        Invitations invitation = invitationService.findById(id);
        if (invitation == null) {
            System.out.println("Invitation not found for ID: " + id);
            return "error";
        }
        System.out.println("Invitation Details: " + invitation);
        model.addAttribute("invitation", invitation);
        return "freelancer/inviteDetails";

    }

}
