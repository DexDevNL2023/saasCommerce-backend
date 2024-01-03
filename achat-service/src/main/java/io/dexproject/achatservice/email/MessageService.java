package com.dexproject.shop.api.email;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Component
public class MessageService {

	@Resource
	private MessageSource messageSource;

	private Locale locale = LocaleContextHolder.getLocale();

	public String getMessage(String code) {
		return messageSource.getMessage(code, null, locale);
	}

	public String getMessage(String code, Object... params) {
		return messageSource.getMessage(code, params, locale);
	}
}
