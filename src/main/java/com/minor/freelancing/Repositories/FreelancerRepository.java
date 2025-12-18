package com.minor.freelancing.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Projects;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Long> {

    Freelancer findByEmail(String email);

    // Projects don't have a direct 'proposals' collection, proposals store
    // projectId/freelancerId.
    // Use a subquery to find projects that have proposals from the given
    // freelancer.
    @Query("select p from Projects p where p.freelancer.id = :freelancerId")
    List<Projects> getProjectsByFreelancerId(Long freelancerId);

    @Query("select f from Freelancer f")
    List<Freelancer> findAllFreelancers();



}
