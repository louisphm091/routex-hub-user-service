package vn.com.routex.hub.user.service.interfaces.models.login;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.user.service.interfaces.models.base.BaseResponse;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class LoginResponse extends BaseResponse<LoginResponse.LoginResponseData> {

    @Getter
    @Setter
    @SuperBuilder
    public static class LoginResponseData {
        private String accessToken;
        private String refreshToken;
        private String userId;
        private String username;
        private Set<String> roles;
        private Set<String> authorities;
    }
}
