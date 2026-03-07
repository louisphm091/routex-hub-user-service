package vn.com.routex.hub.user.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.application.service.EmailService;
import vn.com.routex.hub.user.service.application.service.VerificationService;
import vn.com.routex.hub.user.service.domain.otp.Otp;
import vn.com.routex.hub.user.service.domain.otp.OtpPurpose;
import vn.com.routex.hub.user.service.domain.otp.OtpRepository;
import vn.com.routex.hub.user.service.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.user.service.infrastructure.utils.ExceptionUtils;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpResponse;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.OTP_LENGTH;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_COOL_DOWN;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_COOL_DOWN_MESSAGE;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    private final long RESEND_COOLDOWN_SECONDS = 120;
    private final long EXPIRED_OTP_MINUTES = 20;

    @Override
    public OtpResponse createClientOtp(OtpRequest request) {
        otpRepository.findLatestActiveOtp(request.getData().getUserId(), OtpPurpose.REGSITER_VERIFY)
                .ifPresent(existing -> {
                    if(existing.getProducedAt() != null) {
                        long seconds = Duration.between(existing.getProducedAt(), OffsetDateTime.now()).getSeconds();

                        if (seconds < RESEND_COOLDOWN_SECONDS) {
                            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                                    ExceptionUtils.buildResultResponse(OTP_COOL_DOWN, OTP_COOL_DOWN_MESSAGE));
                        }
                    }
                });

        String plainOtp = generateOtp();

        Otp otp = Otp.builder()
                .id(UUID.randomUUID().toString())
                .userId(request.getData().getUserId())
                .fullName(request.getData().getFullName())
                .phoneNumber(request.getData().getPhoneNumber())
                .email(request.getData().getEmail())
                .purpose(request.getData().getPurpose())
                .expiredAt(OffsetDateTime.now().plusMinutes(20))
                .producedAt(OffsetDateTime.now())
                .otpHash(passwordEncoder.encode(plainOtp))
                .attemptCount(0)
                .purpose(OtpPurpose.REGSITER_VERIFY)
                .expiredAt(OffsetDateTime.now().plusMinutes(EXPIRED_OTP_MINUTES))
                .build();

        otpRepository.save(otp);

        long expiresMinutes = ChronoUnit.MINUTES.between(OffsetDateTime.now(), otp.getExpiredAt());

        return OtpResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .data(OtpResponse.OtpResponseData.builder()
                        .plainOtp(plainOtp)
                        .userId(request.getData().getUserId())
                        .fullName(request.getData().getFullName())
                        .email(request.getData().getEmail())
                        .expiresMinutes(expiresMinutes)
                        .build())
                .build();
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int value = secureRandom.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", value);
    }
}
