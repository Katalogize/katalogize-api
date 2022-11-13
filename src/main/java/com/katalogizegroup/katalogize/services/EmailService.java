package com.katalogizegroup.katalogize.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    private String registerMailHtml = """
            <div style='display: flex; place-content: center;'>
            		<div style='width: max-content; margin: 50px; padding: 50px; border-radius: 10px; box-shadow: 0 1px 10px 0 rgb(0 0 0 / 10%), 0 2px 15px 0 rgb(0 0 0 / 5%);'>
            			<div>
            				<img style='min-width: 150px; margin-top: 25px; margin-bottom: 20px' src='https://storage.googleapis.com/katalogize-files/official/Email/katalogize_logo.png'>
            			</div>
            			<div style='font-size: 21px; font-family: Inter,sans-serif;'>
            				<b>Welcome to Katalogize, USER_NAME!</b>
            				<p>You were registered successfully and can start creating your Katalogs!</p>
            				<p>Katalogize allows you to create catalogs about anything you want, however you need.</p>
            				<p style='text-align:center;  margin: 50px;'><a style="padding: 15px; border-radius: 10px; text-decoration: none; background-color: #279cac; color: white" href='https://katalogize.com' target='_blank' rel='noreferrer'>Visit Katalogize</a><p>
            				<p>Are you a developer? You can also start using our <a style='color: #279cac;' href='https://api.katalogize.com' target='_blank' rel='noreferrer'>Katalogize API</a>.</p>
            				<p>If you have any questions, feel free to email us at: <a href='mailto:katalogize@gmail.com?subject=Katalogize Help Request' style='color: #279cac;'>katalogize@gmail.com</a></p>
            				<div>Thanks,</div>
            				<div style='color: #279cac;'><b>Katalogize Team</b></div>
            				<p style='font-size: 14px;'>If you haven't requested an account, please ignore this mail.</p>
            			</div>
            		<div>
            	</div>
            """;

    private String changePasswordHtml = """
            <div style='display: flex; place-content: center;'>
            		<div style='width: max-content; margin: 50px; padding: 50px; border-radius: 10px; box-shadow: 0 1px 10px 0 rgb(0 0 0 / 10%), 0 2px 15px 0 rgb(0 0 0 / 5%);'>
            			<div>
            				<img style='min-width: 150px; margin-top: 25px; margin-bottom: 20px' src='https://storage.googleapis.com/katalogize-files/official/Email/katalogize_logo.png'>
            			</div>
            			<div style='font-size: 21px; font-family: Inter,sans-serif;'>
            				<b>You requested a password change!</b>
            				<p>Hello USER_NAME, looks like you have requested a new password for you account!</p>
            				<p>Please, enter Katalogize with the following credentials and change your password on the settings page!</p>
            				<p><b>Username:</b> USER_NAME</p>
            				<p><b>Password:</b> NEW_PASSWORD</p>
            				<p style='text-align:center;  margin: 50px;'><a style="padding: 15px; border-radius: 10px; text-decoration: none; background-color: #279cac; color: white" href='https://katalogize.com' target='_blank' rel='noreferrer'>Visit Katalogize</a><p>
            				<p>If you have any questions, feel free to email us at: <a href='mailto:katalogize@gmail.com?subject=Katalogize Help Request' style='color: #279cac;'>katalogize@gmail.com</a></p>
            				<div>Thanks,</div>
            				<div style='color: #279cac;'><b>Katalogize Team</b></div>
            				<p style='font-size: 14px;'>If you haven't requested a password change, please change you password and contact us.</p>
            			</div>
            		<div>
            	</div>
            """;

    public boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }

    public void sendRegistrationEmail (String emailTo, String userName) {
        String message = registerMailHtml.replace("USER_NAME", userName);
        String subject = "Welcome to Katalogize";
        sendEmail(emailTo, subject, message);
    }

    public void sendForgotPasswordEmail (String emailTo, String userName, String newPassword) {
        String message = changePasswordHtml.replace("USER_NAME", userName).replace("NEW_PASSWORD", newPassword);
        String subject = "Katalogize - Change your password";
        sendEmail(emailTo, subject, message);
    }

    public void sendEmail(String emailTo, String subject, String message) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            msgHelper.setTo(emailTo);
            msgHelper.setFrom("katalogize@gmail.com");
            msgHelper.setSubject(subject);
            msgHelper.setText(message, true);
            javaMailSender.send(msgHelper.getMimeMessage());
            log.info("Email sent to " + emailTo);
        } catch (Exception e) {
            log.error("Error while sending email");
        }
    }
}
