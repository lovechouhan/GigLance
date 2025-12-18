package com.minor.freelancing.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minor.freelancing.Entities.Escrow;
import com.minor.freelancing.Helper.PaymentStatus;

@Repository
public interface EscrowRepository extends JpaRepository<Escrow, Long> {

    @Query("SELECT e FROM Escrow e WHERE e.client.id = :id")
    public List<Escrow> findEscrowsByClientId(Long id);

    @Query("SELECT e FROM Escrow e WHERE e.freelancer.id = :id ORDER BY e.createdAt DESC")
    public List<Escrow> findEscrowsByFreelancerId(Long id);

    @Query("SELECT CONCAT(COALESCE(SUM(e.amount), 0), '') FROM Escrow e WHERE e.freelancer.id = :id AND e.paymentInfo.paymentStatus = :completed")
    public String sumAmountsByFreelancerIdAndStatus(Long id, PaymentStatus completed);

}
