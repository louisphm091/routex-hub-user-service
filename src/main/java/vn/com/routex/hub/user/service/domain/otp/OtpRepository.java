package vn.com.routex.hub.user.service.domain.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {

    Optional<Otp> findByUserId(String userId);


    @Query(value = """
            
            SELECT O FROM OTP O
            WHERE O.USER_ID = :userId
            AND O.PURPOSE = :purpose
            AND O.CONSUMED_AT IS NULL
            ORDER BY O.CREATED_AT DESC
            """, nativeQuery = true)
    Optional<Otp> findLatestActiveOtp(String userId, OtpPurpose purpose);

}
