package com.minor.freelancing.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minor.freelancing.Entities.Proposal;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    // use nested property name with underscore so Spring Data resolves to
    // freelancer.id
    List<Proposal> findByFreelancer_Id(Long freelancerId);

    List<Proposal> findByProjectId(Long projectId);

    @Query("select p from Proposal p where p.freelancer.id = :freelancerId")
    List<Proposal> findAllProposalByFreelancerId(Long freelancerId);

    @Query("select p from Proposal p where p.client.id = :id")
    List<Proposal> findAllProposalByClientId(Long id);

    @Query("select count(p) from Proposal p where p.freelancer.id = :id")
    int countProposalsByFreelancerId(Long id);

    @Query("select count(p) from Proposal p where p.project.client.id = :id")
    public Long countProposalsReceivedByClientId(Long id);

    @Query("select count(p) from Proposal p where p.client.id = :id")
    public Long countInvitationsSentByClientId(Long id);
}
