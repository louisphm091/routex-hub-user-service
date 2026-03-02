package vn.com.routex.hub.user.service.infrastructure.utils;

import lombok.experimental.UtilityClass;
import vn.com.routex.hub.user.service.interfaces.models.result.ApiResult;

@UtilityClass
public class ExceptionUtils {

    public ApiResult buildResultResponse(String responseCode, String description) {
        return ApiResult.builder()
                .responseCode(responseCode)
                .description(description)
                .build();
    }
}
