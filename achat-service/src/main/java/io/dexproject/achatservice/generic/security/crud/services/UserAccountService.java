package io.dexproject.achatservice.generic.security.crud.services;

import io.dexproject.achatservice.generic.security.crud.repositories.UserAccountRepository;
import io.dexproject.achatservice.generic.security.crud.repositories.VerifyTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor
public class UserAccountService implements UserDetailsService {

	@Autowired
	MailService mailService;
	private final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
	@Autowired
	private UserAccountConverter userConverter;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private UserAccountRepository userRepository;
	@Autowired
	private VerifyTokenRepository tokenRepository;

	@Override
	public UserAccount registerUser(SignupRequest userForm) {
		//Verifying whether user already exists
		if (userRepository.existsByEmail(userForm.getEmail()))
			throw new ResourceNotFoundException("Email is already in use!");
		if (!userForm.getPasswordTxt().equals(userForm.getMatchingPassword()))
			throw new ResourceNotFoundException("Please confirm your password");

		// Create new user's account
		// Mapper Dto
		UserAccount newUser = userConverter.convertSignupTo(userForm);
		newUser.setPassword(bCryptPasswordEncoder.encode(userForm.getPasswordTxt()));

		// Create user's account
		userRepository.saveAndFlush(newUser);

		// Create cart for user
		if (newUser.getRole().getValue().equalsIgnoreCase("customer")) createCart(newUser);

		if (newUser.isUsingQr()) {
			// Send qrcode login with email
			mailService.sendQrCodeLogin(newUser);
		} else {
			// Genered token
			String token = GeneralUtils.generateTokenNumber();
			VerifyToken myToken = new VerifyToken(newUser, token);
			tokenRepository.saveAndFlush(myToken);

			// Send token activated with email
			mailService.sendVerificationToken(newUser, token);
		}

		return newUser;
	}

	private void createCart(UserAccount newUser) {
		Cart cart = new Cart();
		cart.setClient(newUser);
		// generate a random UUID number 
		cart.setNum(GeneralUtils.generateTokenNumber());
		cartRepository.saveAndFlush(cart);
	}

	@Override
	@Transactional
	public UserAccount processOAuthRegister(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
		if (StringUtils.isEmpty(oAuth2UserInfo.getName())) {
			throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
		} else if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		UserAccount newOAuthUser = toUserRegistrationObject(oAuth2UserInfo, idToken, userInfo);
		if (userRepository.existsByEmail(newOAuthUser.getEmail())) {
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
		existingUser = userRepository.findByEmail(existingUser.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		existingUser.setDisplayName(oAuth2UserInfo.getName());
		userRepository.saveAndFlush(existingUser);
		return existingUser;
	}

	private SignupRequest toSignUpRequestObject(OAuth2UserInfo oAuth2UserInfo) {
		return SignupRequest.getBuilder().addDisplayName(oAuth2UserInfo.getName()).addEmail(oAuth2UserInfo.getEmail()).build();
	}

	@Override
	public String processOAuthLogin(UserDetails userPrincipal) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmail(userPrincipal.getUsername()))
			throw new ResourceNotFoundException("User not existe by this email!");
		UserAccount loginUser = userRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// On vérifie que le compte utilisateur est activé
		if (!loginUser.isActived()) throw new ResourceNotFoundException("User account is not enable!");
		//This constructor can only be used by AuthenticationManager
		// générer le JWT
		String jwt = jwtUtils.generateJwtTokens(loginUser);
		// Update user's account
		loginUser.setAccesToken(jwt);
		loginUser.setConnected(true);
		userRepository.saveAndFlush(loginUser);

		return loginUser.getAccesToken();
	}

	@Override
	public LoginDto loginUser(LoginRequest userForm) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmail(userForm.getEmail()))
			throw new ResourceNotFoundException("User not existe by this email!");
		UserAccount loginUser = userRepository.findByEmail(userForm.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// On vérifie que le compte utilisateur est activé
		if (!loginUser.isActived()) throw new ResourceNotFoundException("User account is not enable!");
		String loginPass = bCryptPasswordEncoder.encode(userForm.getPasswordTxt());
		// le mot de passe est vide, donc le compte a été crée par quelqu'un d'autre et c'est sa première connexion
		if (loginUser.getPassword().isEmpty() || loginUser.getPassword().equals(bCryptPasswordEncoder.encode(""))) {
			// on chiffre et enregistre le mot de passe envoyé
			loginUser.setPassword(loginPass);
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
		LoginDto loginDto = userConverter.convertToLoginDto(loginUser);

		return loginDto;
	}

	@Override
	public LoginDto loginUsingQrCode(String email) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmail(email))
			throw new ResourceNotFoundException("User not existe by this qrcode!");
		UserAccount loginUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
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
		LoginDto loginDto = userConverter.convertToLoginDto(loginUser);

		return loginDto;
	}

	@Override
	public void logoutUser(LoginRequest userForm) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmail(userForm.getEmail()))
			throw new ResourceNotFoundException("User not existe by this email!");
		UserAccount logoutUser = userRepository.findByEmail(userForm.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User is not found."));

		// Update user's account
		logoutUser.setAccesToken("");
		logoutUser.setConnected(false);
		userRepository.saveAndFlush(logoutUser);
		SecurityContextHolder.clearContext();
    }

	@Override
	public void createUser(UserFormRequest userForm) {
		//Verifying whether user already exists
		if (userRepository.existsByEmail(userForm.getEmail()))
			throw new ResourceNotFoundException("Email is already in use!");

		// Create new user's account
		// Mapper Dto
		UserAccount newUser = userConverter.convertFromTo(userForm);
		newUser.setPassword(bCryptPasswordEncoder.encode(""));

		// Create user's account
		userRepository.saveAndFlush(newUser);

		// Create cart for user
		if (newUser.getRole().getValue().equalsIgnoreCase("customer")) createCart(newUser);

    }

	@Override
	public void editUser(UserFormRequest userForm) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmail(userForm.getEmail()))
			throw new ResourceNotFoundException("User not existe by this email!");

		// Create new user's account
		// Mapper Dto
		UserAccount updatedUser = userConverter.convertFromTo(userForm);
		UserAccount findedUser = userRepository.findByEmail(userForm.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		updatedUser.setId(findedUser.getId());

		// Create user's account
		userRepository.saveAndFlush(updatedUser);

    }

	@Override
	public void editPassword(UserFormPasswordRequest userForm) {
		//Verifying whether user already exists
		if (!userRepository.existsByEmail(userForm.getEmail()))
			throw new ResourceNotFoundException("User not existe by this email!");
		UserAccount updatedUser = userRepository.findByEmail(userForm.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		String lastPass = bCryptPasswordEncoder.encode(userForm.getLastPassword());
		if (!lastPass.equals(updatedUser.getPassword()))
			throw new ResourceNotFoundException("Please confirm your last password");

		// Update user's account with new password
		updatedUser.setPassword(bCryptPasswordEncoder.encode(userForm.getNewPassword()));
		userRepository.saveAndFlush(updatedUser);

    }

	@Override
	public void suspendUserById(Long id) {
		//Verifying whether user already exists
		if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not existe by this email!");
		UserAccount updatedUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// Update user's account status
		updatedUser.setActived(false);
		userRepository.saveAndFlush(updatedUser);

    }

	@Override
	public void deleteUserById(Long id) {
		//Verifying whether user already exists
		if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not existe by this email!");
		userRepository.deleteById(id);
    }

	@Override
	public ProfileDto findUserById(Long id) {
		UserAccount userAccount = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
		// Mapper Dto
		ProfileDto profileDto = userConverter.convertToProfileDto(userAccount);
		return profileDto;
	}

	@Override
	public UserAccount loadUserByUserEmail(String useremail) {
		return userRepository.findByEmail(useremail).orElseThrow(() -> new ResourceNotFoundException("User is not found."));
	}

	@Override
	public List<ProfileDto> getAllUsers() {
		List<UserAccount> allUsers = userRepository.findAll();
		// Mapper Dto
		List<ProfileDto> profileListDto = userConverter.convertToProfileListDto(allUsers);
		return profileListDto;
	}

	@Override
	public Long countAllUsers() {
		return userRepository.count();
	}

	@Override
	public Long countUserByRolename(String roleCle) {
		// on récupere le role
		RoleName emRole;
		if (roleCle == null) {
			emRole = RoleName.CUSTOMER;
		} else {
			switch (roleCle) {
				case "customer":
					emRole = RoleName.CUSTOMER;
					break;
				case "merchant":
					emRole = RoleName.MERCHANT;
					break;
				case "admin":
					emRole = RoleName.ADMIN;
					break;
				default:
					emRole = RoleName.CUSTOMER;
					break;
			}
		}
		return userRepository.countByRolename(emRole);
	}

	@Override
	public PagedResponse<ProfileDto> getListUsers(Integer page, Integer size) {
		// Vérifier la syntaxe de page et size
		GeneralUtils.validatePageNumberAndSize(page, size);
		// Construire la pagination
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "create_at");
		Page<UserAccount> allUsers = userRepository.findAll(pageable);
		if (allUsers.getNumberOfElements() == 0) throw new ResourceNotFoundException("User find list is empty!");
		// Mapper Dto
		List<ProfileDto> profileListDto = userConverter.convertToProfilePageDto(allUsers);
		return new PagedResponse<>(profileListDto, allUsers.getNumber(), allUsers.getSize(), allUsers.getTotalElements(), allUsers.getTotalPages(), allUsers.isLast());
	}

	@Override
	public PagedResponse<ProfileDto> getListUsersByRole(String roleCle, Integer page, Integer size) {
		// Vérifier la syntaxe de page et size
		GeneralUtils.validatePageNumberAndSize(page, size);
		// Construire la pagination
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "create_at");
		// on récupere le role
		RoleName emRole;
		if (roleCle == null) {
			emRole = RoleName.CUSTOMER;
		} else {
			switch (roleCle) {
				case "customer":
					emRole = RoleName.CUSTOMER;
					break;
				case "merchant":
					emRole = RoleName.MERCHANT;
					break;
				case "admin":
					emRole = RoleName.ADMIN;
					break;
				default:
					emRole = RoleName.CUSTOMER;
					break;
			}
		}
		Page<UserAccount> allUsers = userRepository.findByRolename(emRole, pageable);
		if (allUsers.getNumberOfElements() == 0) throw new ResourceNotFoundException("User find list is empty!");
		// Mapper Dto
		List<ProfileDto> profileListDto = userConverter.convertToProfilePageDto(allUsers);
		return new PagedResponse<>(profileListDto, allUsers.getNumber(), allUsers.getSize(), allUsers.getTotalElements(), allUsers.getTotalPages(), allUsers.isLast());
	}

	@Override
	public PagedResponse<ProfileDto> getListUsersByNameAndRole(String nameSearch, String roleCle, Integer page, Integer size) {
		// Vérifier la syntaxe de page et size
		GeneralUtils.validatePageNumberAndSize(page, size);
		// Construire la pagination
		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "create_at");
		// on récupere le role
		RoleName emRole;
		if (roleCle == null) {
			emRole = RoleName.CUSTOMER;
		} else {
			switch (roleCle) {
				case "customer":
					emRole = RoleName.CUSTOMER;
					break;
				case "merchant":
					emRole = RoleName.MERCHANT;
					break;
				case "admin":
					emRole = RoleName.ADMIN;
					break;
				default:
					emRole = RoleName.CUSTOMER;
					break;
			}
		}
		Page<UserAccount> allUsers = userRepository.findUserContainDisplaynameAndRolename("%" + nameSearch + "%", emRole, pageable);
		if (allUsers.getNumberOfElements() == 0) throw new ResourceNotFoundException("User find list is empty!");
		// Mapper Dto
		List<ProfileDto> profileListDto = userConverter.convertToProfilePageDto(allUsers);
		return new PagedResponse<>(profileListDto, allUsers.getNumber(), allUsers.getSize(), allUsers.getTotalElements(), allUsers.getTotalPages(), allUsers.isLast());
	}

	@Override
	public void addDefaultUsers(String roleCle) {
		UserAccount user = null;
		String userEmail;
		String userDisplayName;

		// On initialise l'utlisateurroot
		switch (roleCle) {
			case "customer":
				//Verifying whether role already exists
				userDisplayName = "customer";
				userEmail = "customer@shopapp.cm";
				if (!userRepository.existsByEmail(userEmail) && this.countUserByRolename(roleCle) == 0) {
					user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
					user.setRole(RoleName.CUSTOMER);
				}
				break;
			case "merchant":
				//Verifying whether role already exists
				userDisplayName = "merchant";
				userEmail = "merchant@shopapp.cm";
				if (!userRepository.existsByEmail(userEmail) && this.countUserByRolename(roleCle) == 0) {
					user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
					user.setRole(RoleName.MERCHANT);
				}
				break;
			case "admin":
				//Verifying whether role already exists
				userDisplayName = "root";
				userEmail = "root@shopapp.cm";
				if (!userRepository.existsByEmail(userEmail) && this.countUserByRolename(roleCle) == 0) {
					user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
					user.setRole(RoleName.ADMIN);
				}
				break;
			default:
				//Verifying whether role already exists
				userDisplayName = "root";
				userEmail = "root@shopapp.cm";
				if (!userRepository.existsByEmail(userEmail) && this.countUserByRolename(roleCle) == 0) {
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

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserAccount userAccount = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Invalid user authentified"));
		if (userAccount == null) throw new UsernameNotFoundException("Invalid user authentified");
		Collection<? extends GrantedAuthority> authorities = GeneralUtils.buildSimpleGrantedAuthorities(userAccount.getRole());
		return new User(userAccount.getEmail(), userAccount.getPassword(), authorities);
	}

	@Override
	@Transactional
	public Boolean resendVerificationToken(String existingVerificationToken) {
		VerifyToken vToken = tokenRepository.findByToken(existingVerificationToken);
		if (vToken != null) {
			// Genered token
			String token = GeneralUtils.generateTokenNumber();
			vToken.updateToken(token);
			vToken = tokenRepository.saveAndFlush(vToken);

			// Send token activated with email
			mailService.sendVerificationToken(vToken.getUser(), vToken.getToken());
			return true;
		}
		return false;
	}

	@Override
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

	public void forgotPassword(String email) throws ResourceNotFoundException {
		UserAccount userAccount = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Invalid user authentified"));
		if (userAccount != null) {
			// Genered token
			String token = GeneralUtils.generateTokenNumber();
			userAccount.setResetPasswordToken(token);
			userRepository.saveAndFlush(userAccount);

			// Send token activated with email
			mailService.sendForgotPasswordToken(userAccount, token);
		} else {
			throw new ResourceNotFoundException("Could not find any user account with the email " + email);
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
}
