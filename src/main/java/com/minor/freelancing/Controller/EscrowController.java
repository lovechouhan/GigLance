package com.minor.freelancing.Controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.minor.freelancing.Entities.Contract;
import com.minor.freelancing.Entities.Escrow;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Repositories.EscrowRepository;
import com.minor.freelancing.Services.ContractService;
import com.minor.freelancing.Services.EscrowServices;
import com.minor.freelancing.Services.PDFGeneratorService;
import com.minor.freelancing.Services.ProjectService;
import com.minor.freelancing.Services.ProposalService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/escrow")
public class EscrowController {
    @Autowired
    private ProposalService proposalService;
    @Autowired
    private EscrowRepository escrowRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    PDFGeneratorService pdfGeneratorService;

    @Autowired
    private EscrowServices escrowServices;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/openpdf/export/{id}")
    public String paymentSuccessPath(
            @org.springframework.web.bind.annotation.PathVariable("id") Long id,
            @RequestParam(value = "paymentId", required = false) String paymentId,
            Model model) {
        return handlePaymentSuccess(id, paymentId, model);
    }

    @GetMapping("/payment/success")
    public String handlePaymentSuccess(@RequestParam("proposalId") Long proposalId,
            @RequestParam(value = "paymentId", required = false) String paymentId,
            Model model) {
        try {
            Proposal p = proposalService.findById(proposalId).orElse(null);

            if (p != null) {
                Contract contract = contractService.createContractFromProposal(p, p.getProject(), p.getFreelancer());
                escrowServices.createEscrowEntry(p, paymentId, contract);

                proposalService.acceptProposal(proposalId);
                System.out.println("Proposal accepted for id " + proposalId);
                model.addAttribute("paymentId", paymentId);
                model.addAttribute("amount", p.getBidAmount());

                System.out.println("Payment success handled for proposal id " + proposalId);
            } else {
                System.out.println("Payment success: Proposal not found for id " + proposalId);
            }
        } catch (Exception ex) {
            System.out.println("Error in payment success handling: " + ex.getMessage());
        }

        return "Contracts/contracts";
    }

    @GetMapping("/client/receipt/{id}")
    public void createPDF(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        Escrow escrow = escrowServices.findById(id);
        // You may use 'id' to generate or find a specific receipt. Currently generator
        // exports a generic PDF.
        pdfGeneratorService.exportPaymentReceipt(response, escrow);

    }

}