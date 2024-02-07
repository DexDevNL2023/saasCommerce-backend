package io.dexproject.achatservice.security;

import io.dexproject.achatservice.config.MyEndpointsManagementConfig;
import io.dexproject.achatservice.security.crud.enums.RoleName;
import io.dexproject.achatservice.security.crud.services.UserAccountService;
import io.dexproject.achatservice.security.jwt.JwtTokenFilter;
import io.dexproject.achatservice.security.jwt.RestAuthenticationEntryPoint;
import io.dexproject.achatservice.generic.security.oauth2.*;
import io.dexproject.achatservice.security.oauth2.*;
import io.dexproject.achatservice.utils.JwtUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

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
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {
    private final MyEndpointsManagementConfig myEndpointsManagementConfig;
    private final JwtUtils jwtUtils;
    private final UserAccountService userAccountService;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    public SecurityConfig(MyEndpointsManagementConfig myEndpointsManagementConfig, JwtUtils jwtUtils, UserAccountService userAccountService, HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        this.myEndpointsManagementConfig = myEndpointsManagementConfig;
        this.jwtUtils = jwtUtils;
        this.userAccountService = userAccountService;
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Si Spring MVC est sur le chemin de classe et qu'aucun CorsConfigurationSource n'est fourni,
        // Spring Security utilisera la configuration CORS fournie à Spring MVC
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                        .httpStrictTransportSecurity(withDefaults())
                        .xssProtection(withDefaults())
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/api/auth/login")
                        .defaultSuccessUrl("/", true).permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout").permitAll()
                )
                .rememberMe(withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                )
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //Access configuration
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(myEndpointsManagementConfig.getOpenApiEndpoints()).permitAll()
                        .requestMatchers("/management/**").hasAuthority(RoleName.ADMIN.getValue())
                        .anyRequest().authenticated())
                //######## OAUTH2-Login configuration ########
                .oauth2Login(httpSecurityOAuth2LoginConfigurer -> {
                            try {
                                httpSecurityOAuth2LoginConfigurer
                                        .authorizationEndpoint(authorizationEndpointConfig -> {
                                                    try {
                                                        authorizationEndpointConfig
                                                                .baseUri("/api/oauth2/authorize")
                                                                .authorizationRequestRepository(cookieAuthorizationRequestRepository());
                                                    } catch (Exception e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                        )
                                        .redirectionEndpoint(redirectionEndpointConfig -> redirectionEndpointConfig
                                                .baseUri("/api/oauth2/callback/**")
                                        )
                                        .userInfoEndpoint(userInfoEndpointConfig -> {
                                                    try {
                                                        userInfoEndpointConfig
                                                                .oidcUserService(cOidcUserService())
                                                                .userService(cOAuth2UserService());
                                                    } catch (Exception e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }
                                        )
                                        .successHandler(oAuth2SuccessHandler())
                                        .failureHandler(oAuth2FailureHandler());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
        http.setSharedObject(ContentNegotiationStrategy.class, new HeaderContentNegotiationStrategy());
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
        return new JwtTokenFilter(jwtUtils, userAccountService);
    }
    
    @Bean
    protected OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler() throws Exception {
        return new OAuth2AuthenticationSuccessHandler(userAccountService, cookieAuthorizationRequestRepository());
    }
    
    @Bean
    protected OAuth2AuthenticationFailureHandler oAuth2FailureHandler() throws Exception {
        return new OAuth2AuthenticationFailureHandler(httpCookieOAuth2AuthorizationRequestRepository);
    }
    
    @Bean
    protected CustomOAuth2UserService cOAuth2UserService() throws Exception {
        return new CustomOAuth2UserService(userAccountService);
    }
    
    @Bean
    protected CustomOidcUserService cOidcUserService() throws Exception {
        return new CustomOidcUserService(userAccountService);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(CorsEndpointProperties corsProperties) {
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(myEndpointsManagementConfig.getAllowedMapping(), corsProperties.toCorsConfiguration());
        return source;
    }
}
