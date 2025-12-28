package com.cheatCode.cashier.service;

import java.nio.file.FileSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties.Simple;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cheatCode.cashier.model.db_model.saleModel;
import com.cheatCode.cashier.service.Invoice.InvoiceGenerater;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

  @Autowired
  JavaMailSender mailSender;

  @Autowired
  InvoiceGenerater invoiceGenerater;

  @Async("taskExecutor")
  public void sendEmail(String email, String subject, saleModel sale) {
    try {

      String filepath = "invoices/invoice_" + (sale.getId() != null ? sale.getId() : "N_A") + ".pdf";

      // generate pdf
      invoiceGenerater.generateInvoice(filepath, sale, email);

      String body = "Hello,\n\n" +
          "Please find your invoice attached.\n\n" +
          "Thank you for your purchase.";

      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);

      helper.setTo(email);
      helper.setSubject(subject);
      helper.setText(body, false);

      // attach invoice
      FileSystemResource file = new FileSystemResource(filepath);
      helper.addAttachment("invoice.pdf", file);

      mailSender.send(message);
      System.out.println("Email sent successfully" + message);

    } catch (Exception e) {
      // TODO: handle exception
      System.out.println("Email sending failed: " + e.getMessage());
    }

  }
}
