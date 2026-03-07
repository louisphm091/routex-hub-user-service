package vn.com.routex.hub.user.service.interfaces.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.routex.hub.user.service.application.facade.AuthenticationFacade;
import vn.com.routex.hub.user.service.infrastructure.persistence.log.SystemLog;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginRequest;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginResponse;
import vn.com.routex.hub.user.service.interfaces.models.logout.LogoutRequest;
import vn.com.routex.hub.user.service.interfaces.models.logout.LogoutResponse;
import vn.com.routex.hub.user.service.interfaces.models.password.ChangePasswordRequest;
import vn.com.routex.hub.user.service.interfaces.models.password.ChangePasswordResponse;
import vn.com.routex.hub.user.service.interfaces.models.password.ForgotPasswordRequest;
import vn.com.routex.hub.user.service.interfaces.models.password.ForgotPasswordResponse;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationRequest;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationResponse;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeResponse;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.API_PATH;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.API_VERSION;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.AUTHENTICATION;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.CHANGE_PASSWORD;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.FORGOT_PASSWORD;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.LOGIN;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.LOGOUT;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.REGISTER;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.USER_SERVICE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.VERIFY_CODE;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_PATH + API_VERSION + USER_SERVICE)
public class AuthenticationController {

    private final AuthenticationFacade authenticationFacade;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());



    @PostMapping(AUTHENTICATION + REGISTER)
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
        return authenticationFacade.registerUser(request);
    }

    @PostMapping(AUTHENTICATION + VERIFY_CODE)
    public ResponseEntity<VerifyCodeResponse> verifyOtpCode(@Valid @RequestBody VerifyCodeRequest request) {
        return authenticationFacade.verifyOtpCode(request);
    }

    @PostMapping(AUTHENTICATION + LOGIN)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return authenticationFacade.login(request);
    }

    @PostMapping(AUTHENTICATION + CHANGE_PASSWORD)
    public ResponseEntity<ChangePasswordResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return authenticationFacade.changePassword(request);
    }


    @PostMapping(AUTHENTICATION + FORGOT_PASSWORD)
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authenticationFacade.forgotPassword(request);
    }

    @PostMapping(AUTHENTICATION + LOGOUT)
    public ResponseEntity<LogoutResponse> logout(@Valid @RequestBody LogoutRequest request) {
        return authenticationFacade.logout(request);

    }


}
