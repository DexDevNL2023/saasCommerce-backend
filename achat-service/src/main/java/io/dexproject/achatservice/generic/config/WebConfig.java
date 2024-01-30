package io.dexproject.achatservice.generic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${management.endpoints.web.cors.allowed-mapping}")
	private String allowedMapping;
	@Value("${management.endpoints.web.cors.allowed-origins}")
    private String[] allowedOrigins;
	@Value("${management.endpoints.web.cors.allowed-methods}")
	private String alloweMethods;
	@Value("${management.endpoints.web.cors.allowed-headers}")
	private String allowedHeaders;
	@Value("${management.endpoints.web.cors.allowed-credentials}")
	private Boolean allowCredentials;
	@Value("${management.endpoints.web.cors.exposed-headers}")
	private String exposedHeaders;
	@Value("${management.endpoints.web.cors.max-age}")
	private long maxAge;

	private final Environment environment;

	public WebConfig(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(allowedMapping)
                .allowedOrigins(allowedOrigins)
				.allowedMethods(alloweMethods)
				.allowedHeaders(allowedHeaders)
				.allowCredentials(allowCredentials)
				.exposedHeaders(exposedHeaders)
				.maxAge(maxAge);
	}

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		String location = environment.getProperty("app.file.storage.mapping");
		registry.addResourceHandler("/uploads/**").addResourceLocations(location);
	}

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
		// Va intercepter toutes les requetes http://localhost:8080/?language=fr
		// permetant à l'utilisateur de modifier la langue d'affichage des informations
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        registry.addInterceptor(localeChangeInterceptor);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);

		// Gére la convertion des valeurs des énumérateurs en énumérateur
		ApplicationConversionService.configure(registry);
    }

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:i18n/messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(3600);
		return messageSource;
	}

	@Bean
	public LocaleResolver localeResolver() {
		final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.FRENCH);
		return cookieLocaleResolver;
	}

	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource());
		return validator;
	}
}