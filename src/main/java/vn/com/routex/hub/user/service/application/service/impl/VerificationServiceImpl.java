package vn.com.routex.hub.user.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.application.service.EmailService;
import vn.com.routex.hub.user.service.application.service.VerificationService;
import vn.com.routex.hub.user.service.domain.otp.Otp;
import vn.com.routex.hub.user.service.domain.otp.OtpPurpose;
import vn.com.routex.hub.user.service.domain.otp.OtpRepository;
import vn.com.routex.hub.user.service.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.user.service.infrastructure.utils.ExceptionUtils;
import vn.com.routex.hub.user.service.interfaces.models.email.EmailOtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ApiConstant.OTP_LENGTH;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_COOL_DOWN;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_COOL_DOWN_MESSAGE;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {


    @Value("${client.verify.url}")
    private String clientVerifyURL;

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    private final long EXPIRED_OTP_MINUTES = 120;
    private final long RESEND_COOLDOWN_SECONDS = 120;

    @Override
    public Otp createClientOtp(OtpRequest request) {
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
                .phoneNumber(request.getData().getPhoneNumber())
                .email(request.getData().getEmail())
                .purpose(request.getData().getPurpose())
                .producedAt(OffsetDateTime.now())
                .otpHash(passwordEncoder.encode(plainOtp))
                .attemptCount(0)
                .purpose(OtpPurpose.REGSITER_VERIFY)
                .expiredAt(OffsetDateTime.now().plusMinutes(EXPIRED_OTP_MINUTES))
                .build();

        otpRepository.save(otp);

        return otp;
    }

    @Override
    public void sendEmailWithVerification(EmailOtpRequest request) {
        String clientURL = generateVerifyUrl(request.getData().getUserId(), request.getData().getOtpId(), request.getChannel());


    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int value = secureRandom.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", value);
    }

    private String generateVerifyUrl(String userId, String otpId, String channel) {
        return clientVerifyURL
                + "?userId=" + urlEncode(userId)
                + "&otpId=" + urlEncode(otpId)
                + "&channel=" + urlEncode(channel);
    }

    private String urlEncode(String url) {
        try {
            return URLEncoder.encode(url, StandardCharsets.UTF_8);
        } catch(Exception e) {
            return url;
        }
    }

}
