package vn.com.routex.hub.user.service.interfaces.models.login;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.user.service.interfaces.models.base.BaseRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LoginRequest extends BaseRequest {

    @Valid
    @NotNull
    private LoginRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class LoginRequestData {

        @NotBlank
        @NotNull
        private String username;

        @NotBlank
        @NotNull
        private String password;
    }
}
