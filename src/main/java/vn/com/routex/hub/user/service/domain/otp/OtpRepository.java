package vn.com.routex.hub.user.service.domain.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {

    Optional<Otp> findByUserId(String userId);


    @Query("""
    SELECT o FROM Otp o
    WHERE o.userId = :userId
      AND o.purpose = :purpose
      AND o.consumedAt IS NULL
    ORDER BY o.createdAt DESC
        """)
    Optional<Otp> findLatestActiveOtp(
            @Param("userId") String userId,
            @Param("purpose") OtpPurpose purpose
    );

}
