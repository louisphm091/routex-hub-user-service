package vn.com.routex.hub.user.service.application.service.authorization;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.domain.role.Authorities;
import vn.com.routex.hub.user.service.domain.role.RolesRepository;
import vn.com.routex.hub.user.service.domain.role.UserRolesRepository;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserAuthorizationService {

    private final UserRolesRepository userRolesRepository;
    private final RolesRepository rolesRepository;


    public Set<String> getRoles(String userId) {

        return userRolesRepository.findByIdUserId(userId)
                .stream()
                .map(userRole -> rolesRepository.findById(userRole.getId().getRoleId())
                        .orElseThrow())
                .map(role -> "ROLE_" + role.getCode())
                .collect(Collectors.toSet());
    }

    public Set<String> getAuthorities(String userId) {
        return userRolesRepository.findByIdUserId(userId)
                .stream()
                .map(userRole -> rolesRepository.findById(userRole.getId().getRoleId()).orElseThrow())
                .flatMap(role -> role.getAuthorities().stream())
                .map(Authorities::getCode)
                .collect(Collectors.toSet());
    }
}
