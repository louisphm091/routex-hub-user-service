package vn.com.routex.hub.user.service.application.services;

import com.ecommerce.server.domain.auth.entities.User;
import com.ecommerce.server.domain.auth.repository.UserDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Bao Pham
 * @created 07/04/2025
 * @project ecom-hub-product-service
 **/

@Service
public class CustomUserDetailService implements UserDetailsService {


    @Autowired
    private UserDetailRepository userDetailRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDetailRepository.findByEmail(username);

        if(null == user) {
            throw new UsernameNotFoundException("User not found with Username " + username);
        }
        return user;
    }
}
