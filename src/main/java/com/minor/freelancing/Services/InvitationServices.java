package com.minor.freelancing.Services;

import org.springframework.stereotype.Service;

import com.minor.freelancing.DTO.InvitationDTO;
import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Invitations;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Repositories.InvitationRepository;
import com.minor.freelancing.Repositories.ProjectRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.minor.freelancing.Helper.InvitationStatus;

@Service
public class InvitationServices {

  @Autowired
  private InvitationRepository invitationRepository;

  @Autowired
  private ProjectRepository projectRepository;

  public Invitations findById(Long id) {
    if (id == null)
      return null;
    return invitationRepository.findById(id).orElse(null);
  }

  public Invitations sendInvitationToFreelancer(Client client, Freelancer freelancer) {
    Invitations invitation = new Invitations();
    invitation.setClient(client);
    invitation.setFreelancer(freelancer);
    invitation.setStatus(InvitationStatus.PENDING);
    return invitationRepository.save(invitation);
  }

  public void saveInvitation(InvitationDTO invitationDto) {
    // Fetch the Projects object using projectId
    Projects project = null;
    if (invitationDto.getProjectId() != null) {
      project = projectRepository.findById(invitationDto.getProjectId()).orElse(null);
    }

    Invitations existingInvitations = invitationRepository.findById(invitationDto.getId()).orElse(null);
    if (existingInvitations == null) {
      // Prevent exact duplicate invitation for same client, freelancer and project
      if (project != null && invitationDto.getClientName() != null && invitationDto.getFreelancerName() != null) {
        Invitations dup = invitationRepository.findByClientIdAndFreelancerIdAndProjectId(
            invitationDto.getClientName().getId(),
            invitationDto.getFreelancerName().getId(),
            project.getId());
        if (dup != null) {
          throw new IllegalArgumentException(
              "An invitation to this freelancer for the selected project already exists.");
        }
      }
      Invitations newInvitation = new Invitations();
      newInvitation.setProjectTitle(project);
      newInvitation.setClient(invitationDto.getClientName());
      newInvitation.setFreelancer(invitationDto.getFreelancerName());
      newInvitation.setMessage(invitationDto.getMessage());
      newInvitation.setStatus(InvitationStatus.PENDING);
      // newInvitation.setOfferedAmount(invitationDto.getOfferedAmount());
      invitationRepository.save(newInvitation);
      return;
    }
    existingInvitations.setProjectTitle(project);
    // existingInvitations.setClientId(invitationDto.getClientName().getId());
    // existingInvitations.setFreelancerId(invitationDto.getFreelancerName().getId());
    existingInvitations.setMessage(invitationDto.getMessage());
    existingInvitations.setStatus(InvitationStatus.PENDING);
    // existingInvitations.setOfferedAmount(invitationDto.getOfferedAmount());
    invitationRepository.save(existingInvitations);
  }

  public List<Invitations> getInvitationsByFreelancerId(Long id) {

    return invitationRepository.findInvitationsByFreelancerId(id);
  }

  public void save(Invitations invitation) {
    invitationRepository.save(invitation);
  }

  public void updateInviteStatus(Invitations invitation, Projects project) {
    if (project.getStatus().equals("IN_PROGRESS") || project.getStatus().equals("COMPLETED")) {
      invitation.setStatus(InvitationStatus.ACCEPTED);
    }
    invitationRepository.save(invitation);
  }

  public int countInvitationsByFreelancerId(Long id) {
    return invitationRepository.countInvitationsByFreelancerId(id);
  }

}
