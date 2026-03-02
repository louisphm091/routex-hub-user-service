package vn.com.routex.hub.user.service.interfaces.models.verify;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.user.service.domain.otp.OtpPurpose;
import vn.com.routex.hub.user.service.interfaces.models.base.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class VerifyCodeRequest extends BaseRequest {

    private VerifyCodeRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class VerifyCodeRequestData {
        private String userId;
        private String otpId;
        private String otpCode;
        private String phoneNumber;
        private String email;
        private OtpPurpose purpose;
    }
}
