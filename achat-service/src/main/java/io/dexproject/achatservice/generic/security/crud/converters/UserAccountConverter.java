package io.dexproject.achatservice.generic.security.crud.converters;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.LoginReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.UserReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.SignupRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormRequest;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.GenericUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserAccountConverter {

	private final ModelMapper modelMapper;

    public UserAccountConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserAccount convertFromTo(UserFormRequest userForm) {
		UserAccount p = modelMapper.map(userForm, UserAccount.class);
		// On verifie que la langue q ete rensegnee
		if (userForm.getLangKey() == null) {
			p.setLangKey("Fr"); // default language
		} else { // on verifie aue la langue est prise en co;pte dans l'application
			p.setLangKey(GenericUtils.verifieFormatLangue(userForm.getLangKey()));
		}
        // Genered url de login
		final String loginURL = AppConstants.AuthUrl + "usingqr?login=" + p.getEmailOrPhone();
		p.setLoginUrl(loginURL);
	    return p;
	}
	   
	public UserAccount convertSignupTo(SignupRequest userForm) {
		UserAccount p = modelMapper.map(userForm, UserAccount.class);
		// On verifie que la langue q ete rensegnee
		if (userForm.getLangKey() == null) {
			p.setLangKey("Fr"); // default language
		} else { // on verifie aue la langue est prise en co;pte dans l'application
			p.setLangKey(GenericUtils.verifieFormatLangue(userForm.getLangKey()));
		}
        // Genered url de login
		final String loginURL = AppConstants.AuthUrl + "usingqr?login=" + p.getEmailOrPhone();
		p.setLoginUrl(loginURL);
        p.setActived(p.isUsingQr());
	    return p;
	}
	   
	public LoginReponse convertToLoginDto(UserAccount userAccount) {
        return modelMapper.map(userAccount, LoginReponse.class);
	}
	
	public UserReponse convertToUserDto(UserAccount userAccount) {
        return modelMapper.map(userAccount, UserReponse.class);
	}
	
	public List<UserReponse> convertToUserPageDto(Page<UserAccount> userAccountList) {
	    return userAccountList.stream().map(this::convertToUserDto).collect(Collectors.toList());
	}
	
	public List<UserReponse> convertToUserListDto(List<UserAccount> userAccountList) {
	    return userAccountList.stream().map(this::convertToUserDto).collect(Collectors.toList());
	}
}