package vn.com.routex.hub.user.service.application.service;

import vn.com.routex.hub.user.service.domain.otp.Otp;
import vn.com.routex.hub.user.service.interfaces.models.email.EmailOtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.otp.OtpRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeRequest;
import vn.com.routex.hub.user.service.interfaces.models.verify.VerifyCodeResponse;

public interface VerificationService {

    Otp createClientOtp(OtpRequest request);
    void sendEmailWithVerification(EmailOtpRequest request);

}
