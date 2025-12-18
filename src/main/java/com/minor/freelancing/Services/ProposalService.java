package com.minor.freelancing.Services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.minor.freelancing.DTO.ProposalDto;
import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Helper.ProjectStatus;
import com.minor.freelancing.Helper.ProposalStatus;
import com.minor.freelancing.Repositories.ProposalRepository;
@Service
public class ProposalService {

    private Logger logger = LoggerFactory.getLogger(ProposalService.class);
    private final ProposalRepository proposalRepository;


    
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ImageServices imageServices;
    
    @Autowired
    private FreelancerServices freelancerService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private InvitationServices invitationService;

    @Autowired
    private ClientServices clientService;

    private final ContractService contractService;

    @Autowired
    private NotificationService notificationService;

    public ProposalService(ProposalRepository proposalRepository, ContractService contractService) {
        this.proposalRepository = proposalRepository;
        this.contractService = contractService;
    }

    public Proposal create(ProposalDto p, Long freelancerId) throws IOException, Exception {
        // cover letter file
        MultipartFile file = p.getCoverLetter();

        if (p.getProject() == null || p.getProject().getId() == null) {
    throw new RuntimeException("Project not linked with proposal");
}
Long projectId = p.getProject().getId();

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Cover Letter (PDF/DOC) is required.");
        }
        // upload to cloudinary
       Map uploadResult = imageServices.uploadProposalDocument(file);
        System.out.println(uploadResult);
        String fileUrl = uploadResult.get("secure_url").toString();
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();

        if (p.getStatus() == null) p.setStatus(ProposalStatus.PENDING);

       
        Projects project = projectService.getProjectById(projectId);
        Client client = clientService.findClientByProjectId(projectId);
        Freelancer freelancer = freelancerService.findById(freelancerId);
        Proposal proposal = new Proposal();
        // Do NOT set the id for new entities. Let JPA generate it.
        proposal.setFreelancer(freelancer);
        proposal.setClient(client);
        proposal.setProject(project);
        proposal.setCoverLetterUrl(fileUrl);
        proposal.setCoverLetterFileName(fileName);
        proposal.setCoverLetterFileType(fileType);
        proposal.setStatus(p.getStatus());
        proposal.setBidAmount(p.getBidAmount());
        proposal.setTimelineDays(p.getTimelineDays());
        
       
        return proposalRepository.save(proposal);
    }

    public List<Proposal> findByFreelancerId(Long freelancerId) {
        return proposalRepository.findByFreelancer_Id(freelancerId);
    }

    public List<Proposal> findByProjectId(Long projectId) {
        return proposalRepository.findByProjectId(projectId);
    }

    public Optional<Proposal> findById(Long id) {
        return proposalRepository.findById(id);
    }

    public Proposal update(Proposal p) {
        return proposalRepository.save(p);
    }

    public List<Proposal> getAllProposalsDoneByFreelancer() {
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

        Freelancer freelancer = freelancerService.findByEmail(email);
        return proposalRepository.findAllProposalByFreelancerId(freelancer.getId());
    }

    public List<Proposal> getAllProposalsByProjectId(Long id) {
        return proposalRepository.findByProjectId(id);
    }

    public void acceptProposal(Long id) {
        Proposal proposal = proposalRepository.findById(id).orElse(null);
        if (proposal != null) {
            Long projectId = proposal.getProject().getId();
            Freelancer freelancer = proposal.getFreelancer();

            if (freelancer == null) {
                logger.error("Freelancer with name " + freelancer.getName() + " not found.");
                return;
            }
            Projects project = projectService.findById(projectId);
            if (project != null) {
                project.setStatus(ProjectStatus.IN_PROGRESS);
                project.setFreelancer(freelancer);
                // persist the updated project
                projectService.create(project);
            }

            // update proposal status and persist
            proposal.setStatus(ProposalStatus.ACCEPTED);
            proposalRepository.save(proposal);

            // create contract from accepted proposal
            contractService.createContractFromProposal(proposal, project, freelancer);
        }
    }

    public void rejectProposal(Long id) {
        Proposal proposal = proposalRepository.findById(id).orElse(null);
        if (proposal != null) {
            proposal.setStatus(ProposalStatus.REJECTED);
            proposalRepository.save(proposal);
        }

    }

    public List<Proposal> findProposalByFreelancerId(Long id) {
        return proposalRepository.findAllProposalByFreelancerId(id);
    }

    public int countProposalsByFreelancerId(Long id) {
        return proposalRepository.countProposalsByFreelancerId(id);
    }
}
