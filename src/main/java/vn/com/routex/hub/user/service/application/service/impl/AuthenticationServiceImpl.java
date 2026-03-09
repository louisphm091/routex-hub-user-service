package vn.com.routex.hub.user.service.application.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.application.service.AuthenticationService;
import vn.com.routex.hub.user.service.application.service.VerificationService;
import vn.com.routex.hub.user.service.application.service.authorization.UserAuthorizationService;
import vn.com.routex.hub.user.service.application.service.email.EmailService;
import vn.com.routex.hub.user.service.domain.otp.Otp;
import vn.com.routex.hub.user.service.domain.otp.OtpPurpose;
import vn.com.routex.hub.user.service.domain.otp.OtpRepository;
import vn.com.routex.hub.user.service.domain.otp.OtpStatus;
import vn.com.routex.hub.user.service.domain.role.AuthoritiesRepository;
import vn.com.routex.hub.user.service.domain.role.Roles;
import vn.com.routex.hub.user.service.domain.role.RolesList;
import vn.com.routex.hub.user.service.domain.role.RolesRepository;
import vn.com.routex.hub.user.service.domain.role.UserRoleId;
import vn.com.routex.hub.user.service.domain.role.UserRoles;
import vn.com.routex.hub.user.service.domain.role.UserRolesRepository;
import vn.com.routex.hub.user.service.domain.token.RefreshToken;
import vn.com.routex.hub.user.service.domain.token.RefreshTokenRepository;
import vn.com.routex.hub.user.service.domain.token.RefreshTokenStatus;
import vn.com.routex.hub.user.service.domain.user.User;
import vn.com.routex.hub.user.service.domain.user.UserRepository;
import vn.com.routex.hub.user.service.domain.user.UserStatus;
import vn.com.routex.hub.user.service.infrastructure.persistence.constant.BusinessConstant;
import vn.com.routex.hub.user.service.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.user.service.infrastructure.persistence.jwt.JwtService;
import vn.com.routex.hub.user.service.infrastructure.persistence.log.SystemLog;
import vn.com.routex.hub.user.service.infrastructure.utils.ExceptionUtils;
import vn.com.routex.hub.user.service.interfaces.models.base.BaseRequest;
import vn.com.routex.hub.user.service.interfaces.models.email.EmailSendingRequest;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginRequest;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginResponse;
import vn.com.routex.hub.user.service.interfaces.models.logout.LogoutRequest;
import vn.com.routex.hub.user.service.interfaces.models.logout.LogoutResponse;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpResponse;
import vn.com.routex.hub.user.service.interfaces.models.password.ChangePasswordRequest;
import vn.com.routex.hub.user.service.interfaces.models.password.ChangePasswordResponse;
import vn.com.routex.hub.user.service.interfaces.models.password.ForgotPasswordRequest;
import vn.com.routex.hub.user.service.interfaces.models.password.ForgotPasswordResponse;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationRequest;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationResponse;
import vn.com.routex.hub.user.service.interfaces.models.result.ApiResult;
import vn.com.routex.hub.user.service.interfaces.models.token.RefreshTokenRequest;
import vn.com.routex.hub.user.service.interfaces.models.token.RefreshTokenResponse;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeResponse;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.BusinessConstant.MAX_ATTEMPTS_OTP;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.AUTHORIZATION_ERROR;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.CONFIRM_PASSWORD_NOT_MATCHED;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.INVALID_NEW_PASSWORD;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.INVALID_OTP_CODE_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.INVALID_PASSWORD;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.INVALID_REFRESH_TOKEN_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.INVALID_USERNAME_EMAIL_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.INVALID_USERNAME_OR_PASSWORD_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_COOL_DOWN;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_EXPIRED;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_FAIL_ATTEMPTS;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.OTP_NOT_ACTIVE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.PHONE_NUMBER_EXISTS;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.RECORD_NOT_FOUND_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.REFRESH_TOKEN_EXPIRED_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.REFRESH_TOKEN_NOT_FOUND_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.ROLE_NOT_FOUND_ERROR;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.SUCCESS_CODE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.SUCCESS_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.USERNAME_EXISTS;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.USER_EXISTS;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.USER_NOT_ACTIVE_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final AuthoritiesRepository authoritiesRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RolesRepository rolesRepository;
    private final UserRolesRepository userRolesRepository;
    private final UserAuthorizationService userAuthorizationService;



    @Override
    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {

        sLog.info("[REGISTER] Request: {}", request);
        Optional<User> optUser = userRepository.findByEmail(request.getData().getEmail());
        if (optUser.isPresent()) {
            User existUser = optUser.get();
            if (UserStatus.VERIFYING.equals(existUser.getStatus())) {
                generateOtpRequestAndSendMail(request, existUser, OtpPurpose.REGISTER_VERIFY);
            }
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, USER_EXISTS));
        }

        if(userRepository.existsByUsername(request.getData().getUsername())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, USERNAME_EXISTS));
        }

        if(userRepository.existsByPhoneNumber(request.getData().getPhoneNumber())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, PHONE_NUMBER_EXISTS));
        }
        String tenantId = request.getData().getTenantId();
        String timezone = request.getData().getTimeZone();
        String language = request.getData().getLanguage();
        // Register user with VERIFYING Status.
        User registeredUser = User.builder()
                .id(UUID.randomUUID().toString())
                .username(request.getData().getUsername())
                .fullName(request.getData().getFullName())
                .passwordHash(passwordEncoder.encode(request.getData().getPassword()))
                .dob(LocalDate.parse(request.getData().getDob()))
                .phoneNumber(request.getData().getPhoneNumber())
                .phoneVerified(Boolean.FALSE)
                .email(request.getData().getEmail())
                .emailVerified(Boolean.FALSE)
                .status(UserStatus.VERIFYING)
                .createdAt(OffsetDateTime.now())
                .tenantId(tenantId != null ? request.getData().getTenantId() : null)
                .language(language != null ? request.getData().getLanguage() : null)
                .timezone(timezone != null ? request.getData().getTimeZone() : null)
                .build();

        userRepository.save(registeredUser);

        Roles customerRole = rolesRepository.findByCode(RolesList.CUSTOMER.name())
                        .orElseThrow(() -> new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                                ExceptionUtils.buildResultResponse(AUTHORIZATION_ERROR, String.format(ROLE_NOT_FOUND_ERROR, RolesList.CUSTOMER.name()))));

        UserRoleId userRoleId = UserRoleId.builder()
                .userId(registeredUser.getId())
                .roleId(customerRole.getId())
                .build();

        UserRoles userRoles = UserRoles.builder()
                .id(userRoleId)
                .assignedAt(OffsetDateTime.now())
                .build();

        userRolesRepository.save(userRoles);

        generateOtpRequestAndSendMail(request, registeredUser, OtpPurpose.REGISTER_VERIFY);
        /*
        - Call to verificationService for generating the url for verification
        - Enclose the OTP Plain in the email and send for user.
         */

        return RegistrationResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.builder()
                        .responseCode(SUCCESS_CODE)
                        .description(SUCCESS_MESSAGE)
                        .build())
                .data(RegistrationResponse.RegistrationResponseData.builder()
                        .userId(registeredUser.getId())
                        .email(request.getData().getEmail())
                        .phoneNumber(request.getData().getPhoneNumber())
                        .userName(request.getData().getUsername())
                        .status(UserStatus.VERIFYING.name())
                        .build()
                ).build();
    }



    @Override
    public VerifyCodeResponse verifyOtpUser(VerifyCodeRequest request) {

        sLog.info("[VERIFICATION] Verification Request: {}", request);
        Otp otp = otpRepository.findByUserId(request.getData().getUserId())
                .orElseThrow(() -> new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, RECORD_NOT_FOUND_MESSAGE)));

        User user = userRepository.findById(request.getData().getUserId())
                .orElseThrow(() -> new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, USER_NOT_FOUND_MESSAGE)));

        if(!passwordEncoder.matches(request.getData().getOtpCode(), otp.getOtpHash())) {
            otp.setAttemptCount(otp.getAttemptCount() + 1);
            otpRepository.save(otp);

            if(otp.getAttemptCount() >= MAX_ATTEMPTS_OTP) {
                otp.setStatus(OtpStatus.REVOKED);
                generateOtpRequestAndSendMail(request, user, OtpPurpose.REGISTER_VERIFY);
                throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                        ExceptionUtils.buildResultResponse(OTP_COOL_DOWN, OTP_FAIL_ATTEMPTS));
            }

            otpRepository.save(otp);
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_OTP_CODE_MESSAGE));
        }

        if(otp.getExpiredAt() == null || OffsetDateTime.now().isAfter(otp.getExpiredAt())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(OTP_COOL_DOWN, OTP_EXPIRED));
        }

        if(!OtpStatus.ACTIVE.equals(otp.getStatus())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(OTP_COOL_DOWN, OTP_NOT_ACTIVE));
        }

        otp.setConsumedAt(OffsetDateTime.now());
        otp.setStatus(OtpStatus.USED);
        otp.setUpdatedAt(OffsetDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        user.setUpdatedAt(OffsetDateTime.now());
        otpRepository.save(otp);
        userRepository.save(user);
        sLog.info("[VERIFICATION] Your account is verified from now with UserId: {}", request.getData().getUserId());

        return VerifyCodeResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.builder()
                        .responseCode(SUCCESS_CODE)
                        .description(SUCCESS_MESSAGE)
                        .build())
                .data(VerifyCodeResponse.VerifyCodeResponseData.builder()
                        .otpCode(request.getData().getOtpCode())
                        .status(UserStatus.ACTIVE.name())
                        .userId(request.getData().getUserId())
                        .build())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getData().getUsername())
                .orElseThrow(() -> new BusinessException(
                        request.getRequestId(),
                        request.getRequestDateTime(),
                        request.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, USER_NOT_FOUND_MESSAGE)
                ));

        if (!passwordEncoder.matches(request.getData().getPassword(), user.getPasswordHash())) {
            throw new BusinessException(
                    request.getRequestId(),
                    request.getRequestDateTime(),
                    request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_USERNAME_OR_PASSWORD_MESSAGE)
            );
        }

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new BusinessException(
                    request.getRequestId(),
                    request.getRequestDateTime(),
                    request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, USER_NOT_ACTIVE_MESSAGE)
            );
        }

        Set<String> roles = userAuthorizationService.getRoles(user.getId());
        Set<String> authorities = userAuthorizationService.getAuthorities(user.getId());

        // Generate new accessToken & refreshToken everytime login.
        OffsetDateTime now = OffsetDateTime.now();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.save(RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .userId(user.getId())
                .token(refreshToken)
                .status(RefreshTokenStatus.ACTIVE)
                .issuedAt(jwtService.extractIssuedAt(refreshToken))
                .expiredAt(jwtService.extractExpiration(refreshToken))
                .createdAt(now)
                .updatedAt(now)
                .build());

        return LoginResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.builder()
                        .responseCode(SUCCESS_CODE)
                        .description(SUCCESS_MESSAGE)
                        .build())
                .data(LoginResponse.LoginResponseData.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .roles(roles)
                        .authorities(authorities)
                        .build())
                .build();
    }

    @Transactional
    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, USER_NOT_FOUND_MESSAGE)));

        if(!passwordEncoder.matches(user.getPasswordHash(), request.getData().getOldPassword())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_PASSWORD));
        }

        if(request.getData().getNewPassword().equals(request.getData().getOldPassword())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_NEW_PASSWORD));
        }

        if(!request.getData().getNewPassword().equals(request.getData().getConfirmNewPassword())) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, CONFIRM_PASSWORD_NOT_MATCHED));
        }

        // Update password vao database
        user.setPasswordHash(passwordEncoder.encode(request.getData().getNewPassword()));
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);


        // Revoke current refresh token -> force log out
        refreshTokenRepository.updateAllByUserIdAndStatus(
                user.getId(),
                RefreshTokenStatus.ACTIVE,
                RefreshTokenStatus.REVOKED,
                OffsetDateTime.now()
        );

        return ChangePasswordResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.builder()
                        .responseCode(SUCCESS_CODE)
                        .description(SUCCESS_MESSAGE)
                        .build())
                .data(ChangePasswordResponse.ChangePasswordResponseData.builder()
                        .userId(user.getId())
                        .changeAt(user.getUpdatedAt())
                        .build())
                .build();
    }

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        // Check existing username & email;
        User user = userRepository.findByUsernameAndEmail(request.getData().getUsername(), request.getData().getEmail())
                .orElseThrow(() -> new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                        ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_USERNAME_EMAIL_MESSAGE)));

        // Send email with OTP code for Otp Purpose.FORGOT_PASSWORD

        generateOtpRequestAndSendMail(request, user, OtpPurpose.FORGOT_PASSWORD);

        return ForgotPasswordResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.builder()
                        .responseCode(SUCCESS_CODE)
                        .description(SUCCESS_MESSAGE)
                        .build())
                .data(ForgotPasswordResponse.ForgotPasswordResponseData
                        .builder()
                        .userId(user.getId())
                        .expiresMinutes(BusinessConstant.EXPIRED_OTP_MINUTES)
                        .build())
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String rawRefreshToken = request.getData().getRefreshToken();
        OffsetDateTime now = OffsetDateTime.now();

        sLog.info("[AUTH] Refresh Token Request, requestId: {}",
                request.getRequestId());

        // Validation refresh Token
        if(!jwtService.isTokenValid(rawRefreshToken)) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_REFRESH_TOKEN_MESSAGE));
        }

        // Check Token Type
        String tokenType = jwtService.extractTokenType(rawRefreshToken);
        if(!"REFRESH".equals(tokenType)) {
            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, INVALID_REFRESH_TOKEN_MESSAGE));
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(rawRefreshToken)
                .orElseThrow(() -> new BusinessException(
                        request.getRequestId(),
                        request.getRequestDateTime(),
                        request.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, REFRESH_TOKEN_NOT_FOUND_MESSAGE)));

        if(!RefreshTokenStatus.ACTIVE.equals(refreshToken.getStatus())) {
            throw new BusinessException(
                    request.getRequestId(),
                    request.getRequestDateTime(),
                    request.getChannel(),
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, REFRESH_TOKEN_NOT_FOUND_MESSAGE));
        }

        if(refreshToken.getExpiredAt() == null || now.isAfter(refreshToken.getExpiredAt())) {
            refreshToken.setStatus(RefreshTokenStatus.EXPIRED);
            refreshToken.setUpdatedAt(now);
            refreshTokenRepository.save(refreshToken);

            throw new BusinessException(
                    request.getRequestId(),
                    request.getRequestDateTime(),
                    request.getChannel(),
                    ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, REFRESH_TOKEN_EXPIRED_MESSAGE));

        }

        String userId = jwtService.extractUserId(rawRefreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        request.getRequestId(),
                        request.getRequestDateTime(),
                        request.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, USER_NOT_FOUND_MESSAGE)));

        if(!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new BusinessException(
                    request.getRequestId(),
                    request.getRequestDateTime(),
                    request.getChannel(),
                    ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, USER_NOT_ACTIVE_MESSAGE));

        }
        refreshToken.setStatus(RefreshTokenStatus.USED);
        refreshToken.setUsedAt(now);
        refreshToken.setUpdatedAt(now);
        refreshTokenRepository.save(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        OffsetDateTime newRefreshExpiredAt = jwtService.extractExpiration(newRefreshToken);
        OffsetDateTime accessTokenExpiredAt = jwtService.extractExpiration(newAccessToken);

        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .token(newRefreshToken)
                .status(RefreshTokenStatus.ACTIVE)
                .issuedAt(jwtService.extractIssuedAt(newRefreshToken))
                .expiredAt(newRefreshExpiredAt)
                .createdAt(now)
                .updatedAt(now)
                .build());

        sLog.info("[AUTH] Refresh token rotated successfully. userId={}", user.getId());

        // 6. return new accessToken + refreshToken
        return RefreshTokenResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.builder()
                        .responseCode(SUCCESS_CODE)
                        .description(SUCCESS_MESSAGE)
                        .build())
                .data(RefreshTokenResponse.RefreshTokenResponseData.builder()
                        .userId(user.getId())
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .accessTokenExpiredAt(accessTokenExpiredAt)
                        .refreshTokenExpiredAt(newRefreshExpiredAt)
                        .build())
                .build();
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        String refreshToken = request.getData().getRefreshToken();
        OffsetDateTime now = OffsetDateTime.now();

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(
                        request.getRequestId(),
                        request.getRequestDateTime(),
                        request.getChannel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, REFRESH_TOKEN_NOT_FOUND_MESSAGE)
                ));

        // Revoke current refresh token
        if(RefreshTokenStatus.ACTIVE.equals(token.getStatus())) {
            token.setStatus(RefreshTokenStatus.REVOKED);
            token.setRevokedAt(now);
            token.setUpdatedAt(now);
            refreshTokenRepository.save(token);
        }

        return LogoutResponse
                .builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.builder()
                        .responseCode(SUCCESS_CODE)
                        .description(SUCCESS_MESSAGE)
                        .build())
                .build();
    }


    private void generateOtpRequestAndSendMail(BaseRequest request, User existUser, OtpPurpose otpPurpose) {
        OtpRequest otpRequest = OtpRequest.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .data(OtpRequest.OtpRequestData.builder()
                        .userId(existUser.getId())
                        .email(existUser.getEmail())
                        .phoneNumber(existUser.getPhoneNumber())
                        .fullName(existUser.getFullName())
                        .purpose(OtpPurpose.REGISTER_VERIFY)
                        .build())
                .build();

        OtpResponse result = verificationService.createClientOtp(otpRequest, otpPurpose);

        EmailSendingRequest emailSendingRequest = EmailSendingRequest.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .data(EmailSendingRequest.EmailSendingRequestData.builder()
                        .toEmail(result.getData().getEmail())
                        .userId(result.getData().getUserId())
                        .fullName(result.getData().getFullName())
                        .verificationCode(result.getData().getPlainOtp())
                        .expireMinutes(result.getData().getExpiresMinutes())
                        .build())
                .build();

        emailService.sendEmail(emailSendingRequest);
    }

}
