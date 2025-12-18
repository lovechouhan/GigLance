package com.minor.freelancing.Services;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.minor.freelancing.Entities.Escrow;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class PDFGeneratorService {
    public void exportPaymentReceipt(HttpServletResponse response, Escrow escrow) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Header
        Font gigaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        gigaFont.setSize(28);
        Paragraph platformHeading = new Paragraph("GIGLANCE", gigaFont);
        platformHeading.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(platformHeading);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        headerFont.setSize(20);
        Paragraph heading = new Paragraph("Payment Receipt", headerFont);
        heading.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(heading);

        document.add(new Paragraph("\n"));

        // Body Font
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 13);

        document.add(new Paragraph("Receipt ID: " + value(escrow.getPaymentInfo() != null ? escrow.getPaymentInfo().getReceiptId() : "N/A"), bodyFont));
        document.add(new Paragraph("Order ID: " + value(escrow.getPaymentInfo() != null ? escrow.getPaymentInfo().getOrderId() : "N/A"  ), bodyFont));
        document.add(new Paragraph("Amount: ₹" + value(escrow.getAmount()), bodyFont));
        document.add(new Paragraph("Status: " + value(escrow.getStatus()), bodyFont));

        document.add(new Paragraph("\nFreelancer Details:", headerFont));
        document.add(new Paragraph("Name: " + escrow.getFreelancer().getName(), bodyFont));
        document.add(new Paragraph("Email: " + escrow.getFreelancer().getEmail(), bodyFont));

        document.add(new Paragraph("\nClient Details:", headerFont));
        document.add(new Paragraph("Name: " + escrow.getClient().getName(), bodyFont));
        document.add(new Paragraph("Email: " + escrow.getClient().getEmail(), bodyFont));

        document.add(new Paragraph("\nProject Details:", headerFont));
        document.add(new Paragraph("Title: " + escrow.getProject().getTitle(), bodyFont));
        document.add(new Paragraph("Category: " + escrow.getProject().getCategory(), bodyFont));
        document.add(new Paragraph("Description: " + escrow.getProject().getDescription(), bodyFont));

        document.add(new Paragraph("\nDate: " + escrow.getCreatedAt(), bodyFont));

        document.add(new Paragraph("\nPayment Details:", headerFont));

        document.add(new Paragraph("Payment Method: " + 
                value(escrow.getPaymentInfo() != null ? escrow.getPaymentInfo().getPaymentMethod() : "N/A"), bodyFont));

        document.add(new Paragraph("Payment Status: " + 
                value(escrow.getPaymentInfo() != null ? escrow.getPaymentInfo().getPaymentStatus() : "N/A"), bodyFont));

        document.add(new Paragraph("Transaction ID: " + 
                value(escrow.getPaymentInfo() != null ? escrow.getPaymentInfo().getTransactionId() : "N/A"), bodyFont));

        document.add(new Paragraph("Receipt ID: " + 
                value(escrow.getPaymentInfo() != null ? escrow.getPaymentInfo().getReceiptId() : "N/A"), bodyFont));

        document.add(new Paragraph("Order ID: " + 
                value(escrow.getPaymentInfo() != null ? escrow.getPaymentInfo().getOrderId() : "N/A"), bodyFont));

        document.add(new Paragraph("Currency: " + 
                value(escrow.getPaymentInfo() != null ? escrow.getPaymentInfo().getCurrency() : "N/A"), bodyFont));


        document.add(new Paragraph("\nThank you for using our platform!", bodyFont));

        document.close();
    }

    private String value(Object val) {
    return val == null ? "N/A" : val.toString();
}

}