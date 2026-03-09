package vn.com.routex.hub.user.service.infrastructure.persistence.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.com.routex.hub.user.service.application.service.authorization.UserAuthorizationService;
import vn.com.routex.hub.user.service.domain.role.Authorities;
import vn.com.routex.hub.user.service.domain.role.AuthoritiesRepository;
import vn.com.routex.hub.user.service.domain.user.User;
import vn.com.routex.hub.user.service.domain.user.UserRepository;
import vn.com.routex.hub.user.service.infrastructure.persistence.log.SystemLog;

import java.io.IOException;
import java.security.Security;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthoritiesRepository authoritiesRepository;
    private final UserAuthorizationService userAuthorizationService;
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
            String username = claims.get("username", String.class);
            String tokenType = claims.get("type", String.class);

            if("refresh".equals(tokenType)) {
                filterChain.doFilter(request, response);
                return;
            }

            if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();

                Object rolesClaim = claims.get("roles");

                if(rolesClaim instanceof Collection<?> roles) {
                    roles.stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(String::trim)
                            .filter(role -> !role.isBlank())
                            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .forEach(grantedAuthorities::add);
                }

                Object authoritiesClaim = claims.get("authorities");
                if(authoritiesClaim instanceof Collection<?> authorities) {
                    authorities.stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(String::trim)
                            .filter(authority -> !authority.isBlank())
                            .map(SimpleGrantedAuthority::new)
                            .forEach(grantedAuthorities::add);
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                username != null ? username : userId,
                                null,
                                grantedAuthorities
                        );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch(Exception ignore) {
        }

        filterChain.doFilter(request, response);
    }
}
