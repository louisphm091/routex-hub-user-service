package vn.com.routex.hub.user.service.application.facade;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginRequest;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginResponse;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationRequest;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationResponse;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeResponse;

public interface AuthenticationFacade {

    ResponseEntity<RegistrationResponse> registerUser(RegistrationRequest request);
    ResponseEntity<VerifyCodeResponse> verifyOtpCode(VerifyCodeRequest request);

    ResponseEntity<LoginResponse> login(LoginRequest request);
}
