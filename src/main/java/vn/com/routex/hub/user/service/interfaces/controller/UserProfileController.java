package vn.com.routex.hub.user.service.interfaces.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.API_PATH;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.API_VERSION;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.PROFILE_ME_PATH;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.USER_SERVICE;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(API_PATH + API_VERSION + USER_SERVICE)
public class UserProfileController {

    @GetMapping(PROFILE_ME_PATH)
    public Map<String, Object> profile(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities());
    }
}
