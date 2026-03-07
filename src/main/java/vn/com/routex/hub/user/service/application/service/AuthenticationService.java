package vn.com.routex.hub.user.service.application.service;

import vn.com.routex.hub.user.service.interfaces.models.login.LoginRequest;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginResponse;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationRequest;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationResponse;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeResponse;

public interface AuthenticationService {

    RegistrationResponse registerUser(RegistrationRequest request);
    VerifyCodeResponse verifyOtpUser(VerifyCodeRequest request);
    LoginResponse login(LoginRequest request);

}
