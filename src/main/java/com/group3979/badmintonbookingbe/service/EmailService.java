package com.group3979.badmintonbookingbe.service;

import com.group3979.badmintonbookingbe.repository.IAuthenticationRepository;
import com.group3979.badmintonbookingbe.entity.Account;
import com.group3979.badmintonbookingbe.model.request.ResetPasswordRequest;
import com.group3979.badmintonbookingbe.model.response.EmailDetail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    TokenService tokenService;

    @Autowired
    IAuthenticationRepository iAuthenticationRepository;

    @Value("${reset.password.url}")
    private String resetPasswordUrl;

    public void sendMailTemplate(EmailDetail emailDetail, Map<String, Object> variables, String template) {
        try {
            Context context = new Context();
            context.setVariable("resetLink", emailDetail.getLink());
            context.setVariables(variables);

            String text = templateEngine.process(template, context);

            // Creating a simple mail message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            // Setting up necessary details
            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(emailDetail.getRecipient());
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setSubject(emailDetail.getSubject());
            javaMailSender.send(mimeMessage);
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
        }
    }

    public void sendMail(String email, String name) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(email);
        emailDetail.setSubject("Welcome");
        emailDetail.setMsgBody("Welcome to my website");
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        sendMailTemplate(emailDetail, variables, "emailtemplate");
    }

    public void sendPasswordResetMail(ResetPasswordRequest resetPasswordRequest) {
        Account account = iAuthenticationRepository.findAccountByEmail(resetPasswordRequest.getEmail());
        String token = tokenService.generateToken(account,5*60*1000);

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(resetPasswordRequest.getEmail());
        emailDetail.setSubject("Reset password");
        emailDetail.setMsgBody("Reset password");
        // sữa link
        emailDetail.setLink(resetPasswordUrl + token);

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", account.getName());
        sendMailTemplate(emailDetail, variables, "forgotpasswordemailtemplate");

    }
}
