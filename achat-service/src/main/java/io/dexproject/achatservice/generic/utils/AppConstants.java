package io.dexproject.achatservice.generic.utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

public interface AppConstants {
    String JWT_HEADER_NAME="Authorization";
    String SECRET="vnlangessama@gmail.com";
    long EXPIRATION=24 * 60 * 60 * 1000; // 24 hour
    String HEADER_PREFIX="Bearer ";

    String TOKEN_INVALID = "INVALID";
    String TOKEN_EXPIRED = "EXPIRED";
    String TOKEN_VALID = "VALID";
	
    String DEFAULT_PAGE_NUMBER = "0";
    String DEFAULT_PAGE_SIZE = "30";

    int MAX_PAGE_SIZE = 50;
    int TOKEN_EXPIRATION = 60 * 24;

	@Value("${app.base.url}")
    String BaseUrl = "http://localhost";
    @Value("${app.base.port}")
    String BasePort = "9000";
    String fullUrl = BaseUrl + ":" + BasePort;
    String AuthUrl = fullUrl + "/api/auth/";
    String PayPalUrl = fullUrl + "/api/paypal";
    String PayPalUrlSucces = PayPalUrl + "/success";
    String PayPalUrlCancel = PayPalUrl + "/cancel";
    String SUPPORT_EMAIL = "vnlangessama@gmail.com";
    String COMPANY_NAME = "saasCommerce app";

    List<String> SEARCHABLE_FIELDS = Arrays.asList("id", "createdAt");
    String PERIODE_FILTABLE_FIELD = "createdAt";

    int THREAD_NUMBER = 4;

    String SPRING_PROFILE_DEVELOPMENT = "dev";
    String SPRING_PROFILE_TEST = "test";
    String SPRING_PROFILE_PRODUCTION = "prod";

    @Value("${app.default.package.name}")
    String DefaultPackageName = "io.dexproject";

    String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    int cookieExpireSeconds = 180;
}
