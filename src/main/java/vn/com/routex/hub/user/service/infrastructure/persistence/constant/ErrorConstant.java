package vn.com.routex.hub.user.service.infrastructure.persistence.constant;

public class ErrorConstant {


    public static final String SYSTEM_ERROR = "6800";
    public static final String SYSTEM_ERROR_MESSAGE = "System Error";
    public static final String TIMEOUT_ERROR = "0600";
    public static final String TIMEOUT_ERROR_MESSAGE = "Timeout";
    public static final String RECORD_NOT_FOUND = "1407";
    public static final String INVALID_USERNAME_EMAIL_MESSAGE = "Username or email is incorrect";

    public static final String RECORD_NOT_FOUND_MESSAGE = "Record not found";
    public static final String OTP_NOT_FOUND_MESSAGE = "OTP Firm not found";
    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_MESSAGE = "Success";
    public static final String DRIVER_NOT_FOND_MESSAGE = "Driver Profile not found";
    public static final String USER_NOT_FOUND_MESSAGE = "User Profile not found";

    public static final String REFRESH_TOKEN_NOT_FOUND_MESSAGE = "Refresh token not found!";

    public static final String REFRESH_TOKEN_EXPIRED_MESSAGE = "Refresh token is expired";
    public static final String INVALID_HTTP_REQUEST_RESOURCE_ERROR = "4000";
    public static final String INVALID_HTTP_REQUEST_RESOURCE_ERROR_MESSAGE = "Resource %s path is not exists";

    public static final String INVALID_INPUT_ERROR = "0310";
    public static final String INVALID_REFRESH_TOKEN_MESSAGE = "Invalid refresh token";
    public static final String AUTHORIZATION_ERROR = "3200";
    public static final String USER_NOT_ACTIVE_MESSAGE = "User is not active yet";
    public static final String INVALID_USERNAME_OR_PASSWORD_MESSAGE = "Invalid username or password !";
    public static final String INVALID_PASSWORD = "Password is incorrect!";
    public static final String INVALID_NEW_PASSWORD = "New password must be different from current one";
    public static final String CONFIRM_PASSWORD_NOT_MATCHED = "New password & confirm password not matched";
    public static final String INVALID_OTP_CODE_MESSAGE = "Invalid OTP Code";
    public static final String INVALID_INPUT_MESSAGE = "Invalid Input";
    public static final String INVALID_REQUEST_TIMESTAMP = "5186";
    public static final String DUPLICATE_ERROR = "9400";
    public static final String RECORD_EXISTS = "Record already exists";
    public static final String USER_EXISTS = "User already exists";
    public static final String USERNAME_EXISTS = "Username already exists";
    public static final String PHONE_NUMBER_EXISTS = "Phone Number already exists";
    public static final String ROLE_NOT_FOUND_ERROR = "Role %s not found";
    public static final String OTP_COOL_DOWN = "2600";
    public static final String OTP_FAIL_ATTEMPTS = "Error ! You just attempted more than 5 times.";
    public static final String OTP_NOT_ACTIVE = "Your OTP Code is not active";
    public static final String OTP_EXPIRED = "Your OTP Code is expired";
    public static final String OTP_COOL_DOWN_MESSAGE = "You need to wait for 2 minutes before retry";
}
