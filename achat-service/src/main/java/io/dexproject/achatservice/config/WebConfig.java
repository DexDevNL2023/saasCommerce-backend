package io.dexproject.achatservice.config;

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
	private final MyEndpointsManagementConfig myEndpointsManagementConfig;
	private final Environment environment;

	public WebConfig(MyEndpointsManagementConfig myEndpointsManagementConfig, Environment environment) {
        this.myEndpointsManagementConfig = myEndpointsManagementConfig;
        this.environment = environment;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping(myEndpointsManagementConfig.getAllowedMapping())
                .allowedOrigins(myEndpointsManagementConfig.getAllowedOrigins())
				.allowedMethods(myEndpointsManagementConfig.getAlloweMethods())
				.allowedHeaders(myEndpointsManagementConfig.getAllowedHeaders())
				.allowCredentials(myEndpointsManagementConfig.getAllowCredentials())
				.exposedHeaders(myEndpointsManagementConfig.getExposedHeaders())
				.maxAge(myEndpointsManagementConfig.getMaxAge());
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