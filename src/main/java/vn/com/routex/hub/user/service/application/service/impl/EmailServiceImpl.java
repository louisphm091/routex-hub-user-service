package vn.com.routex.hub.user.service.application.service.impl;

import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.application.service.EmailService;
import vn.com.routex.hub.user.service.interfaces.models.email.EmailSendingRequest;


@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(EmailSendingRequest request) {
        String subject = "%s - your account verification form";
    }
}
