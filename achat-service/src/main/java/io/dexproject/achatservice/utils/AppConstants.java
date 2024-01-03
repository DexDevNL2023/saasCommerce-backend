package com.dexproject.shop.api.util;

import org.springframework.beans.factory.annotation.Value;

public interface AppConstants {
	public static final String TOKEN_INVALID = "INVALID";
	public static final String TOKEN_EXPIRED = "EXPIRED";
	public static final String TOKEN_VALID = "VALID";
	
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
    String COMPANY_NAME = "eShop app";
}
