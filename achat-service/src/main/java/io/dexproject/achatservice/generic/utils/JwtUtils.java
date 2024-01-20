package io.dexproject.achatservice.generic.utils;

import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/*
This class has 4 functions:

parse Jwt from HttpServletRequest request
generate a JWT from username, date, expiration, secret
get username from JWT
validate a JWT
*/
@Component
@RequiredArgsConstructor
public class JwtUtils {
	
    // parse JWT token from request
	public String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(AppConstants.JWT_HEADER_NAME);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AppConstants.HEADER_PREFIX)) {
			return bearerToken.substring(AppConstants.HEADER_PREFIX.length());
		}
		return null;
	}

    // generate JWT token
	public String generateJwtTokens(UserAccount user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + AppConstants.EXPIRATION);

		return Jwts.builder()
				.setSubject(user.getEmailOrPhone())
				.claim("role", user.getRole().getLabel())
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, AppConstants.SECRET)
				.compact();
	}
	
	public String getUserNameFromToken(String token) {
		Claims claims = Jwts.parser().setSigningKey(AppConstants.SECRET).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(AppConstants.SECRET).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
		    System.out.println("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
		    System.out.println("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
		    System.out.println("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
		    System.out.println("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
		    System.out.println("JWT claims string is empty.");
		}
		return false;
	}
}