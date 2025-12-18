package com.minor.freelancing.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minor.freelancing.Entities.Invitations;

@Repository
public interface InvitationRepository extends JpaRepository<Invitations, Long> {

    @Query("select i from Invitations i where i.freelancer.id = :id")
    List<Invitations> findInvitationsByFreelancerId(Long id);

    @Query("select count(i) from Invitations i where i.freelancer.id = :id")
    int countInvitationsByFreelancerId(Long id);

    @Query("select i from Invitations i where i.client.id = :clientId and i.freelancer.id = :freelancerId and i.projectTitle.id = :projectId")
    Invitations findByClientIdAndFreelancerIdAndProjectId(Long clientId, Long freelancerId, Long projectId);

}
