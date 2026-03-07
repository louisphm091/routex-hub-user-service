package vn.com.routex.hub.user.service.interfaces.models.otp;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.user.service.interfaces.models.base.BaseResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OtpResponse extends BaseResponse {

    private OtpResponseData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class OtpResponseData {
        private String plainOtp;
        private String fullName;
        private String email;
        private String userId;
        private Long expiresMinutes;
    }
}
