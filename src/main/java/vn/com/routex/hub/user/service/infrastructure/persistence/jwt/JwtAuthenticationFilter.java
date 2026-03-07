package vn.com.routex.hub.user.service.infrastructure.persistence.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.com.routex.hub.user.service.domain.user.Authorities;
import vn.com.routex.hub.user.service.domain.user.AuthoritiesRepository;
import vn.com.routex.hub.user.service.domain.user.UserRepository;
import vn.com.routex.hub.user.service.infrastructure.persistence.log.SystemLog;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if(!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.extractAllClaims(token);
            String userId = claims.getSubject();

            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                userRepository.findById(userId).ifPresent(user -> {

                    List<Authorities> userRoles = authoritiesRepository.findByUserId(user.getId());


                    sLog.info("Roles: {}", userRoles);
                    Collection<SimpleGrantedAuthority> authorities =
                            userRoles.stream()
                                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                                    .collect(Collectors.toSet());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(),
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        } catch(Exception ignore) {}

        filterChain.doFilter(request, response);
    }
}
