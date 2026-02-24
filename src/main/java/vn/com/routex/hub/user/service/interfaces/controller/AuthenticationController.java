package vn.com.routex.hub.user.service.interfaces.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationRequest;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationResponse;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.API_PATH;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.API_VERSION;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.AUTHENTICATION;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.REGISTER;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.USER_SERVICE;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_PATH + API_VERSION + USER_SERVICE)
public class AuthenticationController {

    @PostMapping(AUTHENTICATION + REGISTER)
    public ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest request) {
        return null;
    }
}
