package vn.com.routex.hub.user.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.domain.user.Authorities;
import vn.com.routex.hub.user.service.domain.user.AuthoritiesRepository;
import vn.com.routex.hub.user.service.domain.user.User;
import vn.com.routex.hub.user.service.domain.user.UserRepository;
import vn.com.routex.hub.user.service.domain.user.UserStatus;
import vn.com.routex.hub.user.service.infrastructure.persistence.exception.BusinessException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthoritiesRepository authoritiesRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        List<Authorities> authoritiesList = authoritiesRepository.findByUserId(user.getId());

        Collection<SimpleGrantedAuthority> authorities =
                authoritiesList.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                        .collect(Collectors.toSet());

        if(!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new DisabledException("User is not active");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                authorities
        );
    }
}
