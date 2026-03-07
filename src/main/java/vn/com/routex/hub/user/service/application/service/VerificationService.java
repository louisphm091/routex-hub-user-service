package vn.com.routex.hub.user.service.application.service;

import vn.com.routex.hub.user.service.interfaces.models.otp.OtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpResponse;
import vn.com.routex.hub.user.service.interfaces.models.register.RegistrationRequest;

import java.time.OffsetDateTime;

public interface VerificationService {

    OtpResponse createClientOtp(OtpRequest request);
}
