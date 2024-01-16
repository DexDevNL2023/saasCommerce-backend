package com.dexproject.shop.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.HttpSecurityDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dexproject.shop.api.security.jwt.JwtTokenFilter;
import com.dexproject.shop.api.security.oauth2.CustomOAuth2UserService;
import com.dexproject.shop.api.security.oauth2.CustomOidcUserService;
import com.dexproject.shop.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.dexproject.shop.api.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.dexproject.shop.api.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.dexproject.shop.api.services.UserAccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

/*

Let me explain the code above.

– @EnableWebSecurity allows Spring to find and automatically apply the class to the global Web Security.

– @EnableGlobalMethodSecurity provides AOP security on methods. It enables @PreAuthorize, @PostAuthorize, it also supports JSR-250. You can find more parameters in configuration in Method Security Expressions.

– We override the configure(HttpSecurity http) method from WebSecurityConfigurerAdapter interface. It tells Spring Security how we configure CORS and CSRF, when we want to require all users to be authenticated or not, which filter (AuthTokenFilter) and when we want it to work (filter before UsernamePasswordAuthenticationFilter), which Exception Handler is chosen (AuthEntryPointJwt).

– Spring Security will load User details to perform authentication & authorization. So it has UserDetailsService interface that we need to implement.

– The implementation of UserDetailsService will be used for configuring DaoAuthenticationProvider by AuthenticationManagerBuilder.userDetailsService() method.

– We also need a PasswordEncoder for the DaoAuthenticationProvider. If we don’t specify, it will use plain text.

*/

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	@Autowired
	private UserAccountService userAccountService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Access-Control-Allow-Headers","Access-Control-Allow-Origin","Access-Control-Request-Method","Access-Control-Request-Headers","Origin","Cache-Control","Content-Type","Authorization"));
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        // Si Spring MVC est sur le chemin de classe et qu'aucun CorsConfigurationSource n'est fourni,
        // Spring Security utilisera la configuration CORS fournie à Spring MVC
        http.cors(Customizer.withDefaults())
        //http.cors().and()
			.formLogin().disable()
			.httpBasic().disable()
			.csrf(AbstractHttpConfigurer::disable)
        	.headers().frameOptions().disable().and()
            .authorizeHttpRequests(request -> request.requestMatchers("/auth/**", "/public/**", "/oauth2/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                    .anyRequest().authenticated().requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN))
            .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2Login().authorizationEndpoint().authorizationRequestRepository(cookieAuthorizationRequestRepository()).and()
			.redirectionEndpoint().and()
			.userInfoEndpoint().oidcUserService(cOidcUserService()).userService(cOAuth2UserService()).and()
			.successHandler(oAuth2SuccessHandler()).failureHandler(oAuth2FailureHandler());

		// Add our custom Token based authentication filter
		http.addFilterBefore(authorizationFiler(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

	/*
	 * By default, Spring OAuth2 uses
	 * HttpSessionOAuth2AuthorizationRequestRepository to save the authorization
	 * request. But, since our service is stateless, we can't save it in the
	 * session. We'll save the request in a Base64 encoded cookie instead.
	 */
	@Bean
	public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() throws Exception {
		return new HttpCookieOAuth2AuthorizationRequestRepository();
	}
    
    @Bean
    protected JwtTokenFilter authorizationFiler() throws Exception {
        return new JwtTokenFilter();
    }
    
    @Bean
    protected OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler() throws Exception {
        return new OAuth2AuthenticationSuccessHandler(userAccountService, cookieAuthorizationRequestRepository());
    }
    
    @Bean
    protected OAuth2AuthenticationFailureHandler oAuth2FailureHandler() throws Exception {
        return new OAuth2AuthenticationFailureHandler();
    }
    
    @Bean
    protected CustomOAuth2UserService cOAuth2UserService() throws Exception {
        return new CustomOAuth2UserService(userAccountService);
    }
    
    @Bean
    protected CustomOidcUserService cOidcUserService() throws Exception {
        return new CustomOidcUserService(userAccountService);
    }
}
