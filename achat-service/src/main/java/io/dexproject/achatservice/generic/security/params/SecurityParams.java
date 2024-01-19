package io.dexproject.achatservice.generic.security.params;

public interface SecurityParams {
    String JWT_HEADER_NAME="Authorization";
    String SECRET="vnlangessama@gmail.com";
    long EXPIRATION=24 * 60 * 60 * 1000; // 24 hour
    String HEADER_PREFIX="Bearer ";
}
