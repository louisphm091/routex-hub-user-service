package vn.com.routex.hub.user.service.domain.otp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "OTP")
public class Otp {

    @Id
    private String id;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "PURPOSE", nullable = false)
    private OtpPurpose purpose;

    @Column(name = "OTP_HASH", nullable = false)
    private String otpHash;

    @Column(name = "ATTEMPT_COUNT", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "EXPIRED_AT", nullable = false)
    private OffsetDateTime expiredAt;

    @Column(name = "CONSUMED_AT", nullable = false)
    private OffsetDateTime consumedAt;
}
