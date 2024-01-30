package io.dexproject.achatservice.generic.security.oauth2.users;

import io.dexproject.achatservice.generic.exceptions.OAuth2AuthenticationProcessingException;
import io.dexproject.achatservice.generic.security.crud.enums.SocialProvider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2UserInfoFactory {
	public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		if (registrationId.equalsIgnoreCase(SocialProvider.GOOGLE.getLabel())) {
			return new GoogleOAuth2UserInfo(attributes);
		} else if (registrationId.equalsIgnoreCase(SocialProvider.FACEBOOK.getLabel())) {
			return new FacebookOAuth2UserInfo(attributes);
		} else if (registrationId.equalsIgnoreCase(SocialProvider.GITHUB.getLabel())) {
			return new GithubOAuth2UserInfo(attributes);
		} else if (registrationId.equalsIgnoreCase(SocialProvider.LINKEDIN.getLabel())) {
			return new LinkedinOAuth2UserInfo(attributes);
		} else if (registrationId.equalsIgnoreCase(SocialProvider.TWITTER.getLabel())) {
			return new GithubOAuth2UserInfo(attributes);
		} else {
			throw new OAuth2AuthenticationProcessingException("Désolé! la connexion avec " + registrationId + " n'est pas encore pris en charge.");
		}
	}
}