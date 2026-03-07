package vn.com.routex.hub.user.service.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authorities, Integer> {
    List<Authorities> findByUserId(String id);
}
