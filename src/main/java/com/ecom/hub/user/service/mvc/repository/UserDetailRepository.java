package com.ecom.hub.user.service.mvc.repository;

import com.ecommerce.server.domain.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Bao Pham
 * @created 07/04/2025
 * @project ecom-hub-product-service
 **/


@Repository
public interface UserDetailRepository extends JpaRepository<User, Long> {
    User findByEmail(String username);
}
