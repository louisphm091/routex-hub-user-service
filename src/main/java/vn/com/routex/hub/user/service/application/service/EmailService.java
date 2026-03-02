package vn.com.routex.hub.user.service.application.service;


import vn.com.routex.hub.user.service.interfaces.models.email.EmailSendingRequest;

public interface EmailService {

    void sendEmail(EmailSendingRequest request);

}
