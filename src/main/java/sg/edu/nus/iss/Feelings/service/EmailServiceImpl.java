package sg.edu.nus.iss.Feelings.service;


import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailServiceImpl {
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

   

    // send email with link
    public String sendMailWithLink(String email, String token) throws UnsupportedEncodingException{
        // create mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        String url = "https://feelings-server-production.up.railway.app/api/resetpassword/" + token;


        String subject = "Here is the link to reset your password";
        String content = "<p>Hello,</p>"
        + "<p>You have requested to reset your password.</p>"
        + "<p>Click the link below to change your password:</p>"
        + "<p><a href=\"" + url + "\">Change my password</a></p>"
        + "<br>"
        + "<p>Ignore this email if you do remember your password, "
        + "or you have not made the request.</p>";

        System.out.println("emailSvc content:" + content);

        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom("contact@feelings.com", "Feelings Support");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setText(content, true);
            mimeMessageHelper.setSubject(subject);


             // send mail
            javaMailSender.send(mimeMessage);
            return "We have sent a reset password link to your email. Please check.";
        }catch (MessagingException e){
            return "Error sending mail";
        }

         
    }
}
