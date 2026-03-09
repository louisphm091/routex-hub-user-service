package vn.com.routex.hub.user.service.application.service.email;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.infrastructure.persistence.config.SendGridMailProperties;
import vn.com.routex.hub.user.service.interfaces.models.email.EmailSendingRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailTemplateService emailTemplateService;
    private final SendGridMailProperties properties;
    @Override
    public void sendEmail(EmailSendingRequest request) {

        Map<String, Object> variables = getStringObjectMap(request);

        String htmlBody = emailTemplateService.processTemplate(
                "email/verification-code",
                variables
        );

        Email from = new Email(properties.getFromEmail(), properties.getFromName());
        Email to = new Email(request.getData().getToEmail());
        Content content = new Content("text/html", htmlBody);
        Mail mail = new Mail(from, properties.getVerifySubject(), to, content);

        SendGrid sendGrid = new SendGrid(properties.getApiKey());
        Request emailRequest = new Request();

        try {
            emailRequest.setMethod(Method.POST);
            emailRequest.setEndpoint("mail/send");
            emailRequest.setBody(mail.build());

            Response response = sendGrid.api(emailRequest);

            if (response.getStatusCode() < 200 || response.getStatusCode() >= 300) {
                throw new IllegalStateException(
                        "SendGrid send mail failed. Status=%s, body=%s"
                                .formatted(response.getStatusCode(), response.getBody())
                );
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to send email via SendGrid", ex);
        }
    }

    private @NonNull Map<String, Object> getStringObjectMap(EmailSendingRequest request) {
        String verifyLink =
                properties.getBaseUrl() + "/api/v1/user-service/authentication/verify/" + request.getData().getUserId();

        Map<String, Object> variables = new HashMap<>();
        variables.put("fullName", (request.getData().getFullName() == null || request.getData().getFullName().isBlank()) ? "bạn" : request.getData().getFullName());
        variables.put("otpCode", request.getData().getVerificationCode());
        variables.put("expiredMinutes", request.getData().getExpireMinutes());
        variables.put("verifyLink", verifyLink);
        return variables;
    }
}
