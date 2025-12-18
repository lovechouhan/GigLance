package com.minor.freelancing.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.minor.freelancing.Entities.Contract;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Invitations;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Helper.ContractStatus;
import com.minor.freelancing.Helper.PaymentStatus;
import com.minor.freelancing.Repositories.ContractRepository;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ProjectService projectService;

    public ContractService(ContractRepository contractRepository, ProjectService projectService) {
        this.contractRepository = contractRepository;
        this.projectService = projectService;
    }

    public Optional<Contract> findById(Long id) {
        return contractRepository.findById(id);
    }

    public List<Contract> findByClientId(Long clientId) {
        return contractRepository.findByClientId(clientId);
    }

    public List<Contract> findByFreelancerId(Long freelancerId) {
        return contractRepository.findByFreelancerId(freelancerId);
    }

    public Contract update(Contract c) {
        return contractRepository.save(c);
    }

    public List<Contract> getContractsByFreelancerId(Long freelancerId) {
        return contractRepository.findByFreelancerId(freelancerId);
    }

    public Contract createContractFromProposal(Proposal proposal, Projects project, Freelancer freelancer) {

        Projects p = projectService.findById(project.getId());

        Contract contract = new Contract();
        // Contract c = Contract.builder()
        //         .proposal(proposal)
        //         .project(project)
        //         .createdAt(LocalDateTime.now())
        //         .paymentStatus(PaymentStatus.HOLD)
        //         .paymentMethod("RAZORPAY")
        //         .client(project.getClient())
        //         .freelancer(freelancer)
        //         .amount(proposal.getBidAmount())
        //         .startDate(LocalDate.now())
        //         .status(ContractStatus.ACTIVE)
        //         .deadline(LocalDate.now().plusDays(proposal.getTimelineDays()))
        //         .build();
        contract.setProposal(proposal);
        contract.setProject(project);
        contract.setCreatedAt(LocalDateTime.now());
        contract.setPaymentStatus(PaymentStatus.HOLD);
        contract.setPaymentMethod("RAZORPAY");
        contract.setClient(project.getClient());
        contract.setFreelancer(freelancer);
        contract.setAmount(proposal.getBidAmount());
        contract.setStartDate(LocalDate.now());
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setDeadline(LocalDate.now().plusDays(proposal.getTimelineDays()));
        Contract c = contractRepository.save(contract);
        
        p.setContract(c);
        return c;
    }

    public List<Contract> getContractsByClientId(Long clientId) {
        return contractRepository.findByClientId(clientId);
    }

    public void createContractFromInvitation(Invitations invitation) {
        Contract contract = new Contract();
        Contract c = Contract.builder()
                .project(invitation.getProjectTitle())
                .client(invitation.getClient())
                .freelancer(invitation.getFreelancer())
                // .amount(invitation.getProposedAmount())
                // .deadline(LocalDate.now().plusDays(invitation.getProposedTimelineDays()))
                .startDate(LocalDate.now())
                .status(ContractStatus.ACTIVE)
                .build();
        Projects p = projectService.findById(invitation.getProjectTitle().getId());
        p.setContract(c);
        projectService.update(p);
        contractRepository.save(c);
    }

    public Contract getContractByProjectId(Long projectId) {
        return contractRepository.findContractByProjectId(projectId);
    }

    public Map<String, String> generateSigningLinks(Long contractId) {
        Contract c = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        String clientToken = UUID.randomUUID().toString();
        String freelancerToken = UUID.randomUUID().toString();

        c.setClientSignToken(clientToken);
        c.setFreelancerSignToken(freelancerToken);
        c.setStatus(ContractStatus.PENDING_CLIENT_SIGNATURE);

        contractRepository.save(c);

        String clientLink = "https://giglance.com/contract/" + contractId + "/sign?role=client&token=" + clientToken;
        String freelancerLink = "https://giglance.com/contract/" + contractId + "/sign?role=freelancer&token="
                + freelancerToken;

        return Map.of(
                "clientLink", clientLink,
                "freelancerLink", freelancerLink);
    }

    public void save(Contract c) {
        contractRepository.save(c);
    }

    public List<Contract> getByFreelancerAndStatus(Freelancer freelancer, ContractStatus contractStatus) {
        return contractRepository.findByFreelancerAndStatus(freelancer, contractStatus);
    }

    public List<Contract> findAll() {
        return contractRepository.findAll();
    }
    

}
