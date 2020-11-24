package ch.vindthing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service("emailSenderService")
public class EmailSenderService {

    private JavaMailSenderImpl javaMailSender;

    @Autowired
    public EmailSenderService(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    public MimeMessage createMessage() {
        return javaMailSender.createMimeMessage();
    }

    @Async
    public void sendEmail(MimeMessage email) {
        javaMailSender.send(email);
    }

    /*public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }*/
}