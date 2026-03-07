package vn.com.routex.hub.user.service.application.service.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.com.routex.hub.user.service.application.service.AuthenticationService;
import vn.com.routex.hub.user.service.application.service.EmailService;
import vn.com.routex.hub.user.service.application.service.VerificationService;
import vn.com.routex.hub.user.service.domain.otp.OtpPurpose;
import vn.com.routex.hub.user.service.domain.otp.OtpRepository;
import vn.com.routex.hub.user.service.domain.user.Authorities;
import vn.com.routex.hub.user.service.domain.user.AuthoritiesRepository;
import vn.com.routex.hub.user.service.domain.user.User;
import vn.com.routex.hub.user.service.domain.user.UserRepository;
import vn.com.routex.hub.user.service.domain.user.UserRoles;
import vn.com.routex.hub.user.service.domain.user.UserStatus;
import vn.com.routex.hub.user.service.infrastructure.persistence.exception.BusinessException;
import vn.com.routex.hub.user.service.infrastructure.persistence.jwt.JwtService;
import vn.com.routex.hub.user.service.infrastructure.persistence.log.SystemLog;
import vn.com.routex.hub.user.service.infrastructure.utils.ExceptionUtils;
import vn.com.routex.hub.user.service.interfaces.models.email.EmailSendingRequest;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginRequest;
import vn.com.routex.hub.user.service.interfaces.models.login.LoginResponse;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpResponse;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationRequest;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationResponse;
import vn.com.routex.hub.user.service.interfaces.models.result.ApiResult;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeResponse;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.DUPLICATE_ERROR;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.PHONE_NUMBER_EXISTS;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.SUCCESS_CODE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.SUCCESS_MESSAGE;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.USERNAME_EXISTS;
import static vn.com.routex.hub.user.service.infrastructure.persistence.constant.ErrorConstant.USER_EXISTS;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final SystemLog sLog = SystemLog.getLogger(this.getClass());
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final AuthoritiesRepository authoritiesRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {

        sLog.info("[REGISTER] Request: {}", request);
//        Optional<User> optUser = userRepository.findByEmail(request.getData().getEmail());
//        if (optUser.isPresent()) {
//            User existUser = optUser.get();
//            if (UserStatus.VERIFYING.equals(existUser.getStatus())) {
//                generateOtpRequestAndSendMail(request, existUser);
//            }
//            throw new BusinessException(request.getRequestId(), request.getRequestDateTime(), request.getChannel(),
//                    ExceptionUtils.buildResultResponse(DUPLICATE_ERROR, USER_EXISTS));
//        }

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

        Authorities userRoles = Authorities.builder()
                        .userId(registeredUser.getId())
                                .role(UserRoles.CUSTOMER.name())
                                        .build();

        authoritiesRepository.save(userRoles);
        userRepository.save(registeredUser);

        generateOtpRequestAndSendMail(request, registeredUser);
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
        return null;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getData().getUsername(), request.getData().getPassword())
        );

        User user = userRepository.findByUsername(request.getData().getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));


        Set<String> roles = authoritiesRepository.findByUserId(user.getId())
                        .stream()
                                .map(Authorities::getRole)
                                        .collect(Collectors.toSet());


        sLog.info("[AUTH-SYSTEM] User login successfully");

        return LoginResponse.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .result(ApiResult.builder()
                        .responseCode(SUCCESS_CODE)
                        .description(SUCCESS_MESSAGE)
                        .build())
                .data(LoginResponse.LoginResponseData.builder()
                        .accessToken(jwtService.generateAccessToken(user))
                        .refreshToken(jwtService.generateRefreshToken(user))
                        .tokenType("Bearer")
                        .expiresInSeconds(jwtService.getAccessTokenExpiresInSeconds())
                        .userId(user.getId())
                        .username(user.getUsername())
                        .roles(roles)
                        .build())
                .build();
    }


    private void generateOtpRequestAndSendMail(RegistrationRequest request, User existUser) {
        OtpRequest otpRequest = OtpRequest.builder()
                .requestId(request.getRequestId())
                .requestDateTime(request.getRequestDateTime())
                .channel(request.getChannel())
                .data(OtpRequest.OtpRequestData.builder()
                        .userId(existUser.getId())
                        .email(existUser.getEmail())
                        .phoneNumber(existUser.getPhoneNumber())
                        .fullName(existUser.getFullName())
                        .purpose(OtpPurpose.REGSITER_VERIFY)
                        .build())
                .build();

        OtpResponse result = verificationService.createClientOtp(otpRequest);

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
