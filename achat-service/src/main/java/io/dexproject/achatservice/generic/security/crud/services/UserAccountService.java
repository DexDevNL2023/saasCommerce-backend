package io.dexproject.achatservice.generic.security.crud.services;

import io.dexproject.achatservice.generic.email.MailService;
import io.dexproject.achatservice.generic.exceptions.OAuth2AuthenticationProcessingException;
import io.dexproject.achatservice.generic.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.generic.security.crud.converters.UserAccountConverter;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.LoginReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PagedResponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.UserReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.LoginRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.SignupRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormPasswordRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormRequest;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.security.crud.entities.VerifyToken;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import io.dexproject.achatservice.generic.security.crud.repositories.UserAccountRepository;
import io.dexproject.achatservice.generic.security.crud.repositories.VerifyTokenRepository;
import io.dexproject.achatservice.generic.security.oauth2.users.OAuth2UserInfo;
import io.dexproject.achatservice.generic.security.oauth2.users.OAuth2UserInfoFactory;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.GenericUtils;
import io.dexproject.achatservice.generic.utils.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserAccountService implements UserDetailsService {

	private final MailService mailService;
	private final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
	private final UserAccountConverter userConverter;
	private final JwtUtils jwtUtils;
	private final UserAccountRepository userRepository;
	private final VerifyTokenRepository tokenRepository;

    public UserAccountService(MailService mailService, UserAccountConverter userConverter, JwtUtils jwtUtils, UserAccountRepository userRepository, VerifyTokenRepository tokenRepository) {
        this.mailService = mailService;
        this.userConverter = userConverter;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

	public UserAccount registerUser(SignupRequest userForm) {
		//Verifying whether user already exists
		if (userRepository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
			throw new ResourceNotFoundException("Email or Phone is already in use!");
		// Create new user's account
		// Mapper Dto
		UserAccount newUser = userConverter.convertSignupTo(userForm);
		// Create user's account
		userRepository.saveAndFlush(newUser);
		if (newUser.isUsingQr()) {
			// Send qrcode login with email
			mailService.sendQrCodeLogin(newUser);
		} else {
			// Genered token
			String token = GenericUtils.generateTokenNumber();
			VerifyToken myToken = new VerifyToken(newUser, token);
			tokenRepository.saveAndFlush(myToken);
			// Send token activated with email
			mailService.sendVerificationToken(newUser, token);
		}
		return newUser;
	}

	@Transactional
	public UserAccount processOAuthRegister(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
		if (StringUtils.isEmpty(oAuth2UserInfo.getName())) {
			throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
		} else if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		UserAccount newOAuthUser = toUserRegistrationObject(oAuth2UserInfo, idToken, userInfo);
		if (userRepository.existsByEmailOrPhone(newOAuthUser.getEmail())) {
			newOAuthUser = updateExistingUser(newOAuthUser, oAuth2UserInfo);
		} else {
			SignupRequest oAuthUserForm = toSignUpRequestObject(oAuth2UserInfo);
			newOAuthUser = registerUser(oAuthUserForm);
		}
		return newOAuthUser;
	}

	private UserAccount toUserRegistrationObject(OAuth2UserInfo oAuth2UserInfo, OidcIdToken idToken, OidcUserInfo userInfo) {
		return UserAccount.create(oAuth2UserInfo, idToken, userInfo);
	}

	private UserAccount updateExistingUser(UserAccount existingUser, OAuth2UserInfo oAuth2UserInfo) {
		existingUser = userRepository.findByEmailOrPhone(existingUser.getEmailOrPhone()).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		existingUser.setDisplayName(oAuth2UserInfo.getName());
		existingUser = userRepository.saveAndFlush(existingUser);
		return existingUser;
	}

	private SignupRequest toSignUpRequestObject(OAuth2UserInfo oAuth2UserInfo) {
		return SignupRequest.getBuilder().addLastName(oAuth2UserInfo.getName())
				.addEmail(oAuth2UserInfo.getEmail())
				.addImageUrl(oAuth2UserInfo.getImageUrl())
				.build();
	}

	public String processOAuthLogin(UserDetails userPrincipal) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmailOrPhone(userPrincipal.getUsername()))
			throw new ResourceNotFoundException("User not existe by this email or phone!");
		UserAccount loginUser = userRepository.findByEmailOrPhone(userPrincipal.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// On vérifie que le compte utilisateur est activé
		if (!loginUser.isActived()) throw new ResourceNotFoundException("User account is not enable!");
		//This constructor can only be used by AuthenticationManager
		// générer le JWT
		String jwt = jwtUtils.generateJwtTokens(loginUser);
		// Update user's account
		loginUser.setAccesToken(jwt);
		loginUser.setConnected(true);
		loginUser = userRepository.saveAndFlush(loginUser);
		return loginUser.getAccesToken();
	}

	public LoginReponse loginUser(LoginRequest userForm) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
			throw new ResourceNotFoundException("User not existe by this email or phone!");
		UserAccount loginUser = userRepository.findByEmailOrPhone(userForm.getEmailOrPhone())
				.orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// On vérifie que le compte utilisateur est activé
		if (!loginUser.isActived()) throw new ResourceNotFoundException("User account is not enable!");
		String loginPass = bCryptPasswordEncoder.encode(userForm.getPasswordTxt());
		// le mot de passe est vide, donc le compte a été crée par quelqu'un d'autre et c'est sa première connexion
		if (loginUser.getPassword().isEmpty() ||
				loginUser.getPassword().equals(bCryptPasswordEncoder.encode(""))) {
			// on chiffre et enregistre le mot de passe envoyé
			if (userForm.getGeneratePassword()) {
				loginUser.setPassword(bCryptPasswordEncoder
						.encode(GenericUtils.generatedPassWord()));
			} else {
				loginUser.setPassword(loginPass);
			}
		} else { // le mot de passe a déjà été renseigner, on vérifie si son chiffrement est identique à celui de l'utilisateur enregistré
			if (!loginPass.equals(loginUser.getPassword()))
				throw new ResourceNotFoundException("Login or password is not correct!");
		}
		//This constructor can only be used by AuthenticationManager
		// générer le JWT
		String jwt = jwtUtils.generateJwtTokens(loginUser);
		// Update user's account
		loginUser.setAccesToken(jwt);
		loginUser.setConnected(true);
		userRepository.saveAndFlush(loginUser);
		// Mapper Dto
        return userConverter.convertToLoginDto(loginUser);
	}

	public LoginReponse loginUsingQrCode(String emailOrPhone) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmailOrPhone(emailOrPhone))
			throw new ResourceNotFoundException("User not existe by this qrcode!");
		UserAccount loginUser = userRepository.findByEmailOrPhone(emailOrPhone)
				.orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// On vérifie que le compte utilisateur est activé
		if (!loginUser.isUsingQr())
			throw new ResourceNotFoundException("User account not autorization to login with qr code!");
		// On vérifie que le compte utilisateur est activé
		if (!loginUser.isActived()) throw new ResourceNotFoundException("User account is not enable!");
		//This constructor can only be used by AuthenticationManager
		// générer le JWT
		String jwt = jwtUtils.generateJwtTokens(loginUser);
		// Update user's account
		loginUser.setAccesToken(jwt);
		loginUser.setConnected(true);
		userRepository.saveAndFlush(loginUser);
		// Mapper Dto
        return userConverter.convertToLoginDto(loginUser);
	}

	public void logoutUser(LoginRequest userForm) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
			throw new ResourceNotFoundException("User not existe by this email or phone!");
		UserAccount logoutUser = userRepository.findByEmailOrPhone(userForm.getEmailOrPhone())
				.orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// Update user's account
		logoutUser.setAccesToken("");
		logoutUser.setConnected(false);
		userRepository.saveAndFlush(logoutUser);
		SecurityContextHolder.clearContext();
    }

	public UserReponse createUser(UserFormRequest userForm) {
		//Verifying whether user already exists
		if (userRepository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
			throw new ResourceNotFoundException("Email or Phone is already in use!");
		// Create new user's account
		// Mapper Dto
		UserAccount newUser = userConverter.convertFromTo(userForm);
		newUser.setPassword(bCryptPasswordEncoder.encode(""));
		// Create user's account
		newUser = userRepository.saveAndFlush(newUser);
		// Mapper Dto
		return userConverter.convertToUserDto(newUser);
    }

	public UserReponse editUser(UserFormRequest userForm) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
			throw new ResourceNotFoundException("User not existe by this email or phone!");
		// Create new user's account
		// Mapper Dto
		UserAccount updatedUser = userConverter.convertFromTo(userForm);
		UserAccount findedUser = userRepository.findByEmailOrPhone(userForm.getEmailOrPhone())
				.orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		updatedUser.setId(findedUser.getId());
		// Create user's account
		updatedUser = userRepository.saveAndFlush(updatedUser);
		// Mapper Dto
		return userConverter.convertToUserDto(updatedUser);
    }

	public UserReponse editPassword(UserFormPasswordRequest userForm) {
		if(!userForm.getNewPassword().equals(userForm.getMatchingPassword()))
			throw new ResourceNotFoundException("Please confirm your password");
		//Verifying whether user already exists
		if (!userRepository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
			throw new ResourceNotFoundException("User not existe by this email or phone!");
		UserAccount updatedUser = userRepository.findByEmailOrPhone(userForm.getEmailOrPhone())
				.orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		String lastPass = bCryptPasswordEncoder.encode(userForm.getLastPassword());
		if (!lastPass.equals(updatedUser.getPassword()))
			throw new ResourceNotFoundException("Please confirm your last password");
		// Update user's account with new password
		updatedUser.setPassword(bCryptPasswordEncoder.encode(userForm.getNewPassword()));
		updatedUser = userRepository.saveAndFlush(updatedUser);
		// Mapper Dto
		return userConverter.convertToUserDto(updatedUser);
    }

	public UserReponse suspendUserById(Long id) {
		//Verifying whether user already exists
		if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not existe by this email!");
		UserAccount updatedUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// Update user's account status
		updatedUser.setActived(false);
		updatedUser = userRepository.saveAndFlush(updatedUser);
		// Mapper Dto
		return userConverter.convertToUserDto(updatedUser);
    }

	public void deleteUserById(Long id) {
		//Verifying whether user already exists
		if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not existe by this email!");
		userRepository.deleteById(id);
    }

	public UserReponse findUserById(Long id) {
		UserAccount userAccount = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// Mapper Dto
        return userConverter.convertToUserDto(userAccount);
	}

	public UserAccount loadUserByEmailOrPhone(String emailOrPhone) {
		return userRepository.findByEmailOrPhone(emailOrPhone)
				.orElseThrow(() -> new ResourceNotFoundException("User is not found."));
	}

	public List<UserReponse> getAllUsers() {
        return userConverter.convertToUserListDto(userRepository.findAll());
	}

	public List<UserReponse> search(String motCle) {
		return userConverter.convertToUserListDto(userRepository.search(motCle));
	}

	public PagedResponse<UserReponse> getUsersByPage(Integer page, Integer size) {
		// Vérifier la syntaxe de page et size
		GenericUtils.validatePageNumberAndSize(page, size);
		// Construire la pagination
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.PERIODE_FILTABLE_FIELD);
		Page<UserAccount> allUsers = userRepository.findAll(pageable);
		if (allUsers.getNumberOfElements() == 0) throw new ResourceNotFoundException("User find list is empty!");
		// Mapper Dto
		return new PagedResponse<>(userConverter.convertToUserPageDto(allUsers), allUsers.getNumber(), allUsers.getSize(), allUsers.getTotalElements(), allUsers.getTotalPages(), allUsers.isLast());
	}

	public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
		UserAccount userAccount = userRepository.findByEmailOrPhone(emailOrPhone)
				.orElseThrow(() -> new UsernameNotFoundException("Invalid user authentified"));
		if (userAccount == null) throw new UsernameNotFoundException("Invalid user authentified");
		Collection<? extends GrantedAuthority> authorities = GenericUtils.buildSimpleGrantedAuthorities(userAccount.getRole());
		return new User(userAccount.getEmail(), userAccount.getPassword(), authorities);
	}

	@Transactional
	public Boolean resendVerificationToken(String existingVerificationToken) {
		VerifyToken vToken = tokenRepository.findByToken(existingVerificationToken);
		if (vToken != null) {
			// Genered token
			String token = GenericUtils.generateTokenNumber();
			vToken.updateToken(token);
			vToken = tokenRepository.saveAndFlush(vToken);
			// Send token activated with email
			mailService.sendVerificationToken(vToken.getUser(), vToken.getToken());
			return true;
		}
		return false;
	}

	public String validateVerificationToken(String token) {
		VerifyToken vToken = tokenRepository.findByToken(token);
		if (vToken == null) {
			return AppConstants.TOKEN_INVALID;
		}
		UserAccount user = vToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((vToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			return AppConstants.TOKEN_EXPIRED;
		}
		user.setActived(true);
		tokenRepository.delete(vToken);
		userRepository.saveAndFlush(user);
		return AppConstants.TOKEN_VALID;
	}

	public void forgotPassword(String emailOrPhone) throws ResourceNotFoundException {
		UserAccount userAccount = userRepository.findByEmailOrPhone(emailOrPhone)
				.orElseThrow(() -> new ResourceNotFoundException("Invalid user authentified"));
		if (userAccount != null) {
			// Genered token
			String token = GenericUtils.generateTokenNumber();
			userAccount.setResetPasswordToken(token);
			userRepository.saveAndFlush(userAccount);
			// Send token activated with email
			mailService.sendForgotPasswordToken(userAccount, token);
		} else {
			throw new ResourceNotFoundException("Could not find any user account with the email or phone " + emailOrPhone);
		}
	}

	public void resetPassword(String token) throws ResourceNotFoundException {
		UserAccount userAccount = userRepository.findByResetPasswordToken(token);
		if (userAccount != null) {
			String encodedPassword = bCryptPasswordEncoder.encode("");
			userAccount.setPassword(encodedPassword);
			userAccount.setResetPasswordToken(null);
			userRepository.saveAndFlush(userAccount);
			// Send token activated with email
			mailService.sendResetPassword(userAccount);
		} else {
			throw new ResourceNotFoundException("Could not find any user account with the token " + token);
		}
	}

    public void addDefaultUsers(RoleName roleCle) {
        UserAccount user = null;
        String userEmail;
        String userDisplayName;

        // On initialise l'utlisateurroot
        switch (roleCle) {
            case CUSTOMER:
                //Verifying whether role already exists
                userDisplayName = "customer";
                userEmail = "customer@default.lan";
                if (!userRepository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    user.setRole(RoleName.CUSTOMER);
                }
                break;
            case MERCHANT:
                //Verifying whether role already exists
                userDisplayName = "merchant";
                userEmail = "merchant@default.lan";
                if (!userRepository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    user.setRole(RoleName.MERCHANT);
                }
                break;
            case PARTNER:
                //Verifying whether role already exists
                userDisplayName = "partner";
                userEmail = "partner@default.lan";
                if (!userRepository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    user.setRole(RoleName.ADMIN);
                }
                break;
            default:
                //Verifying whether role already exists
                userDisplayName = "root";
                userEmail = "root@default.lan";
                if (!userRepository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    user.setRole(RoleName.ADMIN);
                }
                break;
        }

        if (user != null) {
            // Genered url de login
            final String loginURL = AppConstants.AuthUrl + "usingqr?email=" + user.getEmail();
            user.setLoginUrl(loginURL);
            userRepository.saveAndFlush(user);
        }
    }
}