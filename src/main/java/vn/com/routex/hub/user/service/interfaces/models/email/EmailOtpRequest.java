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
public class EmailOtpRequest extends BaseRequest {

    private EmailOtpRequestData data;;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class EmailOtpRequestData {
        private String userName;
        private String userId;
        private String email;
        private String otpId;
        private String verifyUrl;
        private OffsetDateTime expiredAt;
    }
}
