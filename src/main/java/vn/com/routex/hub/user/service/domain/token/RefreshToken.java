package vn.com.routex.hub.user.service.domain.token;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.user.service.domain.auditing.AbstractAuditingEntity;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "REFRESH_TOKEN")
public class RefreshToken extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private RefreshTokenStatus status;

    @Column(name = "ISSUED_AT", nullable = false)
    private OffsetDateTime issuedAt;

    @Column(name = "EXPIRED_AT", nullable = false)
    private OffsetDateTime expiredAt;

    @Column(name = "USED_AT")
    private OffsetDateTime usedAt;

    @Column(name = "REVOKED_AT")
    private OffsetDateTime revokedAt;

}
