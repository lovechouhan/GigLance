package com.minor.freelancing.Services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minor.freelancing.Entities.Contract;
import com.minor.freelancing.Entities.Escrow;
import com.minor.freelancing.Entities.PaymentInfo;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Helper.PaymentStatus;
import static com.minor.freelancing.Helper.PaymentStatus.COMPLETED;
import com.minor.freelancing.Repositories.EscrowRepository;

@Service
public class EscrowServices {

    private final Logger logger = LoggerFactory.getLogger(EscrowServices.class);

    @Autowired
    private EscrowRepository escrowRepository;

    @Autowired
    private ContractService contractService;

    public void createEscrowEntry(Proposal p, String paymentId, Contract contract) {
        try {
            Escrow e = new Escrow();
            e.setProposal(p);
            e.setProject(p.getProject());
            e.setClient(p.getProject().getClient());
            e.setFreelancer(p.getFreelancer());
            e.setAmount(p.getBidAmount());
            e.setContract(contract);
            // populate PaymentInfo
            PaymentInfo pi = new PaymentInfo();
            pi.setPaymentMethod("Razorpay"); // adjust as needed: "Credit Card", "PayPal", etc.
            pi.setPaymentStatus(PaymentStatus.HOLD); // e.g., "Pending", "Completed", "Failed"
            pi.setAmount(p.getBidAmount());
            pi.setTransactionId(paymentId); // use paymentId as transaction id
            pi.setCurrency("INR");
            pi.setReceiptId("RCPT" + System.currentTimeMillis()); // or set from gateway response
            pi.setOrderId("ORDER" + System.currentTimeMillis());
            // pi.setUser(p.getProject().getClient().getUser());
            e.setPaymentInfo(pi);
            e.setStatus(PaymentStatus.HOLD);
            escrowRepository.save(e);
        } catch (Exception ex) {
            logger.error("Error creating or saving escrow entry", ex);
            throw ex; // rethrow so callers can react (payment flow should know about failures)
        }
    }

    public List<Escrow> getEscrowsByClientId(Long id) {
        return escrowRepository.findEscrowsByClientId(id);
    }

    public List<Escrow> getEscrowsByFreelancerId(Long id) {
        return escrowRepository.findEscrowsByFreelancerId(id);
    }

    public Escrow findById(Long id) {
        return escrowRepository.findById(id).orElse(null);
    }

    public String getTotalEarningsByFreelancerId(Long id) {
        return escrowRepository.sumAmountsByFreelancerIdAndStatus(id, COMPLETED);
    }

    public List<Escrow> getAllEscrows() {
        return escrowRepository.findAll();
    }

    public void updateEscrow(Escrow escrow) {
        if (escrow != null) {
            escrowRepository.save(escrow);
        }
    }

}
