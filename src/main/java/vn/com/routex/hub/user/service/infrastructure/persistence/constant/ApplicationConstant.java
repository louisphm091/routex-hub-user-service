package vn.com.routex.hub.user.service.infrastructure.persistence.constant;

public class ApplicationConstant {

    public static final String REQUEST_ID_REGREX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    public static final String DATETIME_REGREX = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}[-+]\\d{2}:\\d{2}";
    public static final String CHANNEL_REGREX = "^(ONL|OFF)$";
}
