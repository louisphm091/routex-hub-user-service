package com.ecom.hub.user.service.mvc.helper;

import java.util.Random;

/**
 * @author Bao Pham
 * @created 07/04/2025
 * @project ecom-hub-product-service
 **/



public class VerificationCodeGenerator {
    public static String generateCode() {
        Random random = new Random();

        int code = 10000 + random.nextInt(900000);
        return String.valueOf(code);



    }


}
