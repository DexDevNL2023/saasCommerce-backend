package com.dexproject.shop.api.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.dexproject.shop.api.entities.enums.RoleName;
import com.dexproject.shop.api.exceptions.ResourceNotFoundException;


public class GeneralUtils {

	public static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final RoleName role) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role.getValue()));
		return authorities;
	}

	public static void validatePageNumberAndSize(final Integer page, final Integer size) {
        if(page < 0) {
            throw new ResourceNotFoundException("Page number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new ResourceNotFoundException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
	}
	
	public static Date calculateExpiryDate(final int expiryTimeInMinutes) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(new Date().getTime());
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		return new Date(cal.getTime().getTime());
	}
	
	public static String generateTokenNumber() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
