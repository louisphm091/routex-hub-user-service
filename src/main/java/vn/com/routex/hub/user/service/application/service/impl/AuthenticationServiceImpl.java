package vn.com.routex.hub.user.service.application.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.application.service.AuthenticationService;
import vn.com.routex.hub.user.service.application.service.VerificationService;
import vn.com.routex.hub.user.service.domain.otp.Otp;
import vn.com.routex.hub.user.service.domain.otp.OtpPurpose;
import vn.com.routex.hub.user.service.domain.otp.OtpRepository;
import vn.com.routex.hub.user.service.domain.user.User;
import vn.com.routex.hub.user.service.domain.user.UserRepository;
import vn.com.routex.hub.user.service.domain.user.UserRoles;
import vn.com.routex.hub.user.service.domain.user.UserStatus;
import vn.com.routex.hub.user.service.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.user.service.infrastructure.persistence.log.SystemLog;
import vn.com.routex.hub.user.service.infrastructure.utils.ExceptionUtils;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationRequest;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationResponse;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeResponse;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_NOT_FOUND_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.USER_EXISTS;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Value("${client.verify.url}")
    private String clientVerifyURL;

    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {

        Optional<User> optUser = userRepository.findByEmail(request.getData().getEmail());
        if (optUser.isPresent()) {
            User existUser = optUser.get();
            if (UserStatus.VERIFYING.equals(existUser.getStatus())) {
                Otp otpFirm = otpRepository.findByUserId(existUser.getId())
                        .orElseThrow(() -> new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                                ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, OTP_NOT_FOUND_MESSAGE)));

//                verificationService.sendVerificationCode(request);

                // Send verification email for user.
            }
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, USER_EXISTS));
        }

        if(userRepository.existsByUsername(request.getData().getUserName())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, USER_EXISTS));
        }

        if(userRepository.existsByPhoneNumber(request.getData().getPhoneNumber())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, USER_EXISTS));
        }


        String tenantId = request.getData().getTenantId();
        String timezone = request.getData().getTimeZone();
        String language = request.getData().getLanguage();

        // Register user with VERIFYING Status.
        User registeredUser = User.builder()
                .id(UUID.randomUUID().toString())
                .username(request.getData().getUserName())
                .fullName(request.getData().getFullName())
                .passwordHash(passwordEncoder.encode(request.getData().getPassword()))
                .dob(LocalDate.parse(request.getData().getDob()))
                .phoneNumber(request.getData().getPhoneNumber())
                .phoneVerified(Boolean.FALSE)
                .email(request.getData().getEmail())
                .emailVerified(Boolean.FALSE)
                .status(UserStatus.VERIFYING)
                .role(UserRoles.GLOBAL_USER)
                .tenantId(tenantId != null ? request.getData().getTenantId() : null)
                .language(language != null ? request.getData().getLanguage() : null)
                .timezone(timezone != null ? request.getData().getTimeZone() : null)
                .build();

        userRepository.save(registeredUser);

        // Generate Plain OTP and send email;
        OtpRequest otpRequest = OtpRequest
                .builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .data(OtpRequest.OtpRequestData
                        .builder()
                        .email(request.getData().getEmail())
                        .phoneNumber(request.getData().getPhoneNumber())
                        .userId(registeredUser.getId())
                        .purpose(OtpPurpose.REGSITER_VERIFY)
                        .build())
                .build();

        Otp otp = verificationService.createClientOtp(otpRequest);

        /*
        - Call to verificationService for generating the url for verification
        - Enclose the OTP Plain in the email and send for user.
         */


        return RegistrationResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .data(RegistrationResponse.RegistrationResponseData.builder()
                        .userId(registeredUser.getId())
                        .email(request.getData().getEmail())
                        .phoneNumber(request.getData().getPhoneNumber())
                        .userName(request.getData().getUserName())
                        .status(UserStatus.VERIFYING.name())
                        .otpId(otp.getId())
                        .build()
                ).build();
    }

    @Override
    public VerifyCodeResponse verifyOtpUser(VerifyCodeRequest request) {
        return null;
    }

}
