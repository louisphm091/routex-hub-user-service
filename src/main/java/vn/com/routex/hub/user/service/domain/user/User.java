package vn.com.routex.hub.user.service.domain.user;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.user.service.domain.auditing.AbstractAuditingEntity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "SYS_USER")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User extends AbstractAuditingEntity {

    @Id
    private String id;

    @Column(name = "USER_NAME", nullable = false)
    private String username;

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dob;

    @Column(name = "PHONE_NUMBER", nullable = false)
    private String phoneNumber;

    @Column(name = "PHONE_VERIFIED")
    private Boolean phoneVerified = false;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "EMAIL_VERIFIED")
    private Boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private UserStatus status;

    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "TIME_ZONE")
    private String timezone;

    @Column(name = "FAIL_LOGIN_COUNT")
    private Integer failLoginCount = 0;

    @Column(name = "LAST_LOGIN_AT")
    private OffsetDateTime lastLoginAt;

    @Column(name = "LOCKED_UNTIL")
    private OffsetDateTime lockedUntil;
}
