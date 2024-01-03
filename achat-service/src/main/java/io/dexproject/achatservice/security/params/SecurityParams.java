package com.dexproject.shop.api.security.params;

public interface SecurityParams {
    public static final String JWT_HEADER_NAME="Authorization";
    public static final String SECRET="vnlangessama@gmail.com";
    public static final long EXPIRATION=24 * 60 * 60 * 1000; // 24 hour
    public static final String HEADER_PREFIX="Bearer ";
}
