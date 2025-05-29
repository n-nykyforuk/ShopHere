package online.shop.shophere.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ShopHere - Password Reset Verification Code");
        message.setText("Your one-time verification code to reset your password is: " + code + "\n\nThis code is valid for 5 minutes.");
        mailSender.send(message);
    }
}