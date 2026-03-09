package vn.com.routex.hub.user.service.application.service.email;


import vn.com.routex.hub.user.service.interfaces.models.email.EmailSendingRequest;

public interface EmailService {

    void sendEmail(EmailSendingRequest request);

}
