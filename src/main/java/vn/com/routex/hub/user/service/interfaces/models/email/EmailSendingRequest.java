package vn.com.routex.hub.user.service.interfaces.models.email;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.user.service.interfaces.models.base.BaseRequest;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EmailSendingRequest extends BaseRequest {
    private EmailSendingRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class EmailSendingRequestData {
        private String toEmail;
        private String fullName;
        private String verificationCode;
        private Long expireMinutes;
        private String userId;
    }
}
