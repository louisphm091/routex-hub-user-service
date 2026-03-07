package vn.com.routex.hub.user.service.interfaces.models.register;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.user.service.interfaces.models.base.BaseRequest;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApplicationConstant.DATE_MONTH_YEAR_REGEX;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApplicationConstant.ONLY_CHARACTER_REGEX;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RegistrationRequest extends BaseRequest {

    @Valid
    @NotNull
    private RegistrationRequestData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class RegistrationRequestData {

        @NotNull
        @NotBlank
        @Size(min = 3, max = 20)
        private String username;

        @NotNull
        @NotBlank
        @Size(min = 3, max = 20)
        private String password;

        @Email
        @NotNull
        @NotBlank
        @Size(max = 200)
        private String email;

        @NotNull
        @NotBlank
        @Size(max = 10)
        private String phoneNumber;


        @NotNull
        @NotBlank
        @Pattern(regexp = ONLY_CHARACTER_REGEX, message = "Only characters are allowed")
        private String fullName;

        @NotNull
        @NotBlank
        @Pattern(regexp = DATE_MONTH_YEAR_REGEX, message = "must be in format of yyyy-MM-dd")
        private String dob;

        private String language;
        private String tenantId;
        private String timeZone;
    }
}
