package com.ecom.hub.user.service.mvc.controller;

import com.ecommerce.server.domain.auth.dto.UserDetailsDTO;
import com.ecommerce.server.domain.auth.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * @author Bao Pham
 * @created 08/04/2025
 * @project ecom-hub-product-service
 **/

@RestController
@RequestMapping("/api/user")
public class UserDetailController {

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/profile")
    public ResponseEntity<UserDetailsDTO> getUseProfile(Principal principal) {

        User user = (User) userDetailsService.loadUserByUsername(principal.getName());

        if(null == user) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UserDetailsDTO userDetailsDTO = UserDetailsDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .addressList(user.getAddressList())
                .authorityList(user.getAuthorities().toArray()).build();

        return new ResponseEntity<>(userDetailsDTO, HttpStatus.OK);
    }

}
