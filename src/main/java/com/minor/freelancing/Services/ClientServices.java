package com.minor.freelancing.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.minor.freelancing.CommonMethod.CommonMethodService;
import com.minor.freelancing.DTO.ProjectDto;
import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Repositories.ClientRepository;
import com.minor.freelancing.Repositories.ContractRepository;
import com.minor.freelancing.Repositories.FreelancerRepository;
import com.minor.freelancing.Repositories.ProjectRepository;
import com.minor.freelancing.Repositories.ProposalRepository;
import com.minor.freelancing.Repositories.UserRepository;

@Service
public class ClientServices {

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ContractRepository contractRepo;

    @Autowired
    private FreelancerRepository freelancerRepo;

    @Autowired
    private ProposalRepository proposalRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonMethodService commonMethodService;

    public Client findById(Long id) {

        return clientRepo.findById(id).orElse(null);
    }

    public void postProject(ProjectDto projectDto) {

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
        User user = userRepo.findByEmail(email);
        if (user == null) {
            System.out.println("Authenticated user not found in database: " + email);
            return; // nothing we can do
        }
        System.out.println("Authenticated user: " + user.getEmail());

        // Try to find the corresponding Client row. With JOINED inheritance, JPA should
        // return
        // the subclass when appropriate, but some legacy users may exist only in the
        // base users
        // table. Handle both cases safely.
        Client client = clientRepo.findByEmail(user.getEmail());
        if (client == null) {
            System.out.println("No Client entity found for user: " + user.getEmail());
            return; // cannot post project without a Client entity
        }
        System.out.println("Client found: " + client.getEmail());
        Projects project = Projects.builder()
                .title(projectDto.getTitle())
                .description(projectDto.getDescription())
                .client(client)
                .budget(projectDto.getBudget())
                .category(projectDto.getCategory())
                .deadline(projectDto.getDeadline())
                .status("OPEN") // default status
                .postedBy(user)
                .build();

        projectRepo.save(project);

        System.out.println("Project posted: " + projectDto);
    }

    public List<Projects> getAllProjectsByClientId(Long clientId) {

        return projectRepo.findAllProjectsByClientId(clientId);
    }

    public Client findByEmail(String email) {

        return clientRepo.findByEmail(email);
    }

    public Authentication getAuthenticatedClient() {
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
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }

        return auth;
    }

    public String getAmountSpentByClientId(Long clientId) {
        Client client = clientRepo.findById(clientId).orElse(null);
        if (client == null) {
            return "0";
        }
        Double totalAmount = contractRepo.calculateTotalAmountByClientId(clientId);
        if (totalAmount == null) {
            return "0";
        }
        return String.format("%.2f", totalAmount);
    }

    public int getActiveProjects() {
        Authentication auth = getAuthenticatedClient();
        if (auth == null)
            return 0;
        Client client = clientRepo.findByEmail(auth.getName());
        if (client == null)
            return 0;
        List<Projects> projects = projectRepo.findAllProjectsByClientId(client.getId());
        int count = 0;
        for (Projects p : projects) {
            if (p.getStatus() == null)
                continue;
            if (!"COMPLETED".equalsIgnoreCase(p.getStatus()))
                count++;
        }
        return count;
    }

    public int getCompletedProjects() {
        Authentication auth = getAuthenticatedClient();
        if (auth == null)
            return 0;
        Client client = clientRepo.findByEmail(auth.getName());
        if (client == null)
            return 0;
        List<Projects> projects = projectRepo.findAllProjectsByClientId(client.getId());
        int count = 0;
        for (Projects p : projects) {
            if (p.getStatus() != null && "COMPLETED".equalsIgnoreCase(p.getStatus()))
                count++;
        }
        return count;
    }

    public int getProposalsReceived() {
        Authentication auth = getAuthenticatedClient();
        if (auth == null)
            return 0;
        Client client = clientRepo.findByEmail(auth.getName());
        if (client == null)
            return 0;
        List<Projects> projects = projectRepo.findAllProjectsByClientId(client.getId());
        int total = 0;
        for (Projects proj : projects) {
            List<com.minor.freelancing.Entities.Proposal> props = proposalRepo.findByProjectId(proj.getId());
            total += props.size();
        }
        return total;
    }

    public int getProposalsAccepted() {
        Authentication auth = getAuthenticatedClient();
        if (auth == null)
            return 0;
        Client client = clientRepo.findByEmail(auth.getName());
        if (client == null)
            return 0;
        List<Projects> projects = projectRepo.findAllProjectsByClientId(client.getId());
        int total = 0;
        for (Projects proj : projects) {
            List<Proposal> props = proposalRepo.findByProjectId(proj.getId());
            for (Proposal p : props) {
                if (p.getStatus() != null && p.getStatus().toString().equalsIgnoreCase("ACCEPTED")) {
                    total++;
                }
            }
        }
        return total;
    }

    public Client findClientByProjectId(Long projectId) { // receive projectId
        return projectRepo.findClientByProjectId(projectId);
    }

    public List<Freelancer> getAllFreelancers() {
        return freelancerRepo.findAllFreelancers();
    }

    public List<Projects> getAllProjectsByClientIdandStatus(Long id) {
        return projectRepo.findAllProjectsByClientIdAndStatus(id);
    }

    public List<Projects> getAllActiveProjectsByClientId(Long id) {
        return projectRepo.findActiveProjectsByClientId(id);
    }

    public Long getAllActiveProjectsCountByClientId(Long id) {
        return projectRepo.countActiveProjectsByClientId(id);
    }

    public Long getProposalsReceivedbyClientId(Long id) {
        return proposalRepo.countProposalsReceivedByClientId(id);
    }

    public Long getInvitationsSentByClientId(Long id) {
        return proposalRepo.countInvitationsSentByClientId(id);
    }

    public void updateClientProfile(Long id, Client updatedClient, MultipartFile profileImage) {

        Client existingClient = clientRepo.findById(id).orElse(null);
        if (existingClient != null) {
            existingClient.setName(updatedClient.getName());
            existingClient.setPhone(updatedClient.getPhone());
            existingClient.setWebsite(updatedClient.getWebsite());
            existingClient.setBio(updatedClient.getBio());

            String imageUrl = existingClient.getImageUrl();
            if (profileImage != null && !profileImage.isEmpty()) {
                try {
                    // Convert image to base64 or save to cloud storage
                    imageUrl = commonMethodService.uploadImageToCloudinary(profileImage);
                    if (imageUrl != null) {
                        existingClient.setImageUrl(imageUrl);
                    }
                } catch (Exception e) {
                    System.out.println("Error uploading image: " + e.getMessage());
                }
            }
            existingClient.setImageUrl(imageUrl);
            // Handle profile image update if needed
            // For example, save the image to a file storage and set the URL/path
            clientRepo.save(existingClient);
        }
    }

    public Client findByUserId(Long id) {
        return clientRepo.findByUser_Id(id);
    }

}
