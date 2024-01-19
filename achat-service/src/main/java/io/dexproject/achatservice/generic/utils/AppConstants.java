package io.dexproject.achatservice.generic.utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

public interface AppConstants {
    String TOKEN_INVALID = "INVALID";
    String TOKEN_EXPIRED = "EXPIRED";
    String TOKEN_VALID = "VALID";
	
    String DEFAULT_PAGE_NUMBER = "0";
    String DEFAULT_PAGE_SIZE = "30";

    int MAX_PAGE_SIZE = 50;
    int EXPIRATION = 60 * 24;

	@Value("${app.base.url}")
    String BaseUrl = "http://localhost:9000";
    String AuthUrl = BaseUrl + "/api/auth/";
    String PayPalUrl = BaseUrl + "/api/paypal";
    String PayPalUrlSucces = PayPalUrl + "/success";
    String PayPalUrlCancel = PayPalUrl + "/cancel";
    String SUPPORT_EMAIL = "vnlangessama@gmail.com";
    String COMPANY_NAME = "saasCommerce app";

    List<String> SEARCHABLE_FIELDS = Arrays.asList("id", "createdAt");
    String PERIODE_FILTABLE_FIELD = "createdAt";
    String CODE_FILTABLE_FIELD = "numOrder";
}
