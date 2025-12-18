package com.minor.freelancing.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minor.freelancing.Entities.Contract;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Helper.ContractStatus;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByClientId(Long clientId);

    @Query("select c from Contract c where c.freelancer.id = :freelancerId")
    List<Contract> findByFreelancerId(Long freelancerId);

    @Query("SELECT c FROM Contract c WHERE c.freelancer = :freelancer AND c.status = :status")
    List<Contract> findByFreelancerAndStatus(Freelancer freelancer, ContractStatus status);

    // Return the most recently created contract for a project (if multiple exist)

    @Query("SELECT SUM(c.amount) FROM Contract c WHERE c.client.id = :clientId")
    public Double calculateTotalAmountByClientId(Long clientId);

    @Query("SELECT c FROM Contract c WHERE c.project.id = :projectId")
    Contract findContractByProjectId(Long projectId);
}
