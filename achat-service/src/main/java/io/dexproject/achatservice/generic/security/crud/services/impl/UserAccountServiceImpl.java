package io.dexproject.achatservice.generic.security.crud.services.impl;

import io.dexproject.achatservice.generic.email.MailService;
import io.dexproject.achatservice.generic.exceptions.OAuth2AuthenticationProcessingException;
import io.dexproject.achatservice.generic.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.generic.mapper.GenericMapper;
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
import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
import io.dexproject.achatservice.generic.security.oauth2.users.OAuth2UserInfo;
import io.dexproject.achatservice.generic.security.oauth2.users.OAuth2UserInfoFactory;
import io.dexproject.achatservice.generic.service.impl.ServiceGenericImpl;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.GenericUtils;
import io.dexproject.achatservice.generic.utils.JwtUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
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
public class UserAccountServiceImpl extends ServiceGenericImpl<UserFormRequest, UserReponse, UserAccount> implements UserAccountService, UserDetailsService {

    private final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
    private final MailService mailService;
    private final UserAccountConverter userConverter;
    private final JwtUtils jwtUtils;
    private final UserAccountRepository repository;
    private final VerifyTokenRepository tokenRepository;

    public UserAccountServiceImpl(JpaEntityInformation<UserAccount, Long> entityInformation, UserAccountRepository repository, GenericMapper<UserFormRequest, UserReponse, UserAccount> mapper, MailService mailService, UserAccountConverter userConverter, JwtUtils jwtUtils, VerifyTokenRepository tokenRepository) {
        super(entityInformation, repository, mapper);
        this.mailService = mailService;
        this.userConverter = userConverter;
        this.jwtUtils = jwtUtils;
        this.repository = repository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public UserAccount registerUser(SignupRequest userForm) {
        //Verifying whether user already exists
        if (repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new ResourceNotFoundException("L'e-mail ou le téléphone est déjà utilisé!");
        // Create new user's account
        // Mapper Dto
        UserAccount newUser = userConverter.convertSignupTo(userForm);
        // Create user's account
        repository.saveAndFlush(newUser);
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

    @Override
    @Transactional
    public UserAccount processOAuthRegister(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
        if (StringUtils.isEmpty(oAuth2UserInfo.getName())) {
            throw new OAuth2AuthenticationProcessingException("Le nom " + oAuth2UserInfo.getName() + " introuvable auprès du fournisseur OAuth2");
        } else if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("E-mail " + oAuth2UserInfo.getEmail() + " introuvable auprès du fournisseur OAuth2");
        }
        UserAccount newOAuthUser = toUserRegistrationObject(oAuth2UserInfo, idToken, userInfo);
        if (repository.existsByEmailOrPhone(newOAuthUser.getEmail())) {
            newOAuthUser = updateExistingUser(newOAuthUser, oAuth2UserInfo);
        } else {
            SignupRequest oAuthUserForm = toSignUpRequestObject(oAuth2UserInfo);
            newOAuthUser = registerUser(oAuthUserForm);
        }
        return newOAuthUser;
    }

    @Override
    public String processOAuthLogin(UserDetails userPrincipal) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userPrincipal.getUsername()))
            throw new ResourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        UserAccount loginUser = repository.findByEmailOrPhone(userPrincipal.getUsername()).orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        // On vérifie que le compte utilisateur est activé
        if (!loginUser.isActived()) throw new ResourceNotFoundException("Le compte utilisateur n'est pas activé!");
        //This constructor can only be used by AuthenticationManager
        // générer le JWT
        String jwt = jwtUtils.generateJwtTokens(loginUser);
        // Update user's account
        loginUser.setAccesToken(jwt);
        loginUser.setConnected(true);
        loginUser = repository.saveAndFlush(loginUser);
        return loginUser.getAccesToken();
    }

    @Override
    public LoginReponse loginUser(LoginRequest userForm) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new ResourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        UserAccount loginUser = repository.findByEmailOrPhone(userForm.getEmailOrPhone())
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        // On vérifie que le compte utilisateur est activé
        if (!loginUser.isActived()) throw new ResourceNotFoundException("Le compte utilisateur n'est pas activé!");
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
                throw new ResourceNotFoundException("Le login ou le mot de passe n'est pas correct!");
        }
        //This constructor can only be used by AuthenticationManager
        // générer le JWT
        String jwt = jwtUtils.generateJwtTokens(loginUser);
        // Update user's account
        loginUser.setAccesToken(jwt);
        loginUser.setConnected(true);
        repository.saveAndFlush(loginUser);
        // Mapper Dto
        return userConverter.convertToLoginDto(loginUser);
    }

    @Override
    public LoginReponse loginUsingQrCode(String emailOrPhone) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(emailOrPhone))
            throw new ResourceNotFoundException("L'utilisateur n'existe pas par ce code QR!");
        UserAccount loginUser = repository.findByEmailOrPhone(emailOrPhone)
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        // On vérifie que le compte utilisateur est activé
        if (!loginUser.isUsingQr())
            throw new ResourceNotFoundException("Compte utilisateur non autorisé à se connecter avec le code QR!");
        // On vérifie que le compte utilisateur est activé
        if (!loginUser.isActived()) throw new ResourceNotFoundException("Le compte utilisateur n'est pas activé!");
        //This constructor can only be used by AuthenticationManager
        // générer le JWT
        String jwt = jwtUtils.generateJwtTokens(loginUser);
        // Update user's account
        loginUser.setAccesToken(jwt);
        loginUser.setConnected(true);
        repository.saveAndFlush(loginUser);
        // Mapper Dto
        return userConverter.convertToLoginDto(loginUser);
    }

    @Override
    public void logoutUser(LoginRequest userForm) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new ResourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        UserAccount logoutUser = repository.findByEmailOrPhone(userForm.getEmailOrPhone())
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        // Update user's account
        logoutUser.setAccesToken("");
        logoutUser.setConnected(false);
        repository.saveAndFlush(logoutUser);
        SecurityContextHolder.clearContext();
    }

    @Override
    public UserReponse createUser(UserFormRequest userForm) {
        //Verifying whether user already exists
        if (repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new ResourceNotFoundException("L'e-mail ou le téléphone est déjà utilisé!");
        // Create new user's account
        // Mapper Dto
        UserAccount newUser = userConverter.convertFromTo(userForm);
        newUser.setPassword(bCryptPasswordEncoder.encode(""));
        // Create user's account
        newUser = repository.saveAndFlush(newUser);
        // Mapper Dto
        return userConverter.convertToUserDto(newUser);
    }

    @Override
    public UserReponse editUser(UserFormRequest userForm) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new ResourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        // Create new user's account
        // Mapper Dto
        UserAccount updatedUser = userConverter.convertFromTo(userForm);
        UserAccount findedUser = repository.findByEmailOrPhone(userForm.getEmailOrPhone())
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        updatedUser.setId(findedUser.getId());
        // Create user's account
        updatedUser = repository.saveAndFlush(updatedUser);
        // Mapper Dto
        return userConverter.convertToUserDto(updatedUser);
    }

    @Override
    public UserReponse editPassword(UserFormPasswordRequest userForm) {
        if (!userForm.getNewPassword().equals(userForm.getMatchingPassword()))
            throw new ResourceNotFoundException("Veuillez confirmer votre mot de passe!");
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new ResourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        UserAccount updatedUser = repository.findByEmailOrPhone(userForm.getEmailOrPhone())
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        String lastPass = bCryptPasswordEncoder.encode(userForm.getLastPassword());
        if (!lastPass.equals(updatedUser.getPassword()))
            throw new ResourceNotFoundException("Veuillez confirmer votre dernier mot de passe!");
        // Update user's account with new password
        updatedUser.setPassword(bCryptPasswordEncoder.encode(userForm.getNewPassword()));
        updatedUser = repository.saveAndFlush(updatedUser);
        // Mapper Dto
        return userConverter.convertToUserDto(updatedUser);
    }

    @Override
    public UserReponse suspendUserById(Long id) {
        //Verifying whether user already exists
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("L'utilisateur n'existe pas avec cet identifiant " + id);
        UserAccount updatedUser = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        // Update user's account status
        updatedUser.setActived(false);
        updatedUser = repository.saveAndFlush(updatedUser);
        // Mapper Dto
        return userConverter.convertToUserDto(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        //Verifying whether user already exists
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("L'utilisateur n'existe pas avec cet identifiant " + id);
        repository.deleteById(id);
    }

    @Override
    public UserReponse findUserById(Long id) {
        UserAccount userAccount = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        // Mapper Dto
        return userConverter.convertToUserDto(userAccount);
    }

    @Override
    public UserAccount loadUserByEmailOrPhone(String emailOrPhone) {
        return repository.findByEmailOrPhone(emailOrPhone)
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        UserAccount userAccount = repository.findByEmailOrPhone(emailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException("Nom utilisateur invalide"));
        if (userAccount == null) throw new UsernameNotFoundException("Nom utilisateur invalide");
        Collection<? extends GrantedAuthority> authorities = GenericUtils.buildSimpleGrantedAuthorities(userAccount.getRole());
        return new User(userAccount.getEmailOrPhone(), userAccount.getPassword(), authorities);
    }

    @Override
    public List<UserReponse> getAllUsers() {
        return userConverter.convertToUserListDto(repository.findAll());
    }

    @Override
    public PagedResponse<UserReponse> getUsersByPage(Integer page, Integer size) {
        // Vérifier la syntaxe de page et size
        GenericUtils.validatePageNumberAndSize(page, size);
        // Construire la pagination
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.PERIODE_FILTABLE_FIELD);
        Page<UserAccount> allUsers = repository.findAll(pageable);
        if (allUsers.getNumberOfElements() == 0)
            throw new ResourceNotFoundException("La liste de recherche d'utilisateurs est vide!");
        // Mapper Dto
        return new PagedResponse<>(userConverter.convertToUserPageDto(allUsers), allUsers.getNumber(), allUsers.getSize(), allUsers.getTotalElements(), allUsers.getTotalPages(), allUsers.isLast());
    }

    @Override
    public List<UserReponse> search(String motCle) {
        return userConverter.convertToUserListDto(repository.search(motCle));
    }

    @Override
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
        repository.saveAndFlush(user);
        return AppConstants.TOKEN_VALID;
    }

    @Override
    public void forgotPassword(String email) throws ResourceNotFoundException {
        if (!GenericUtils.isValidEmailAddress(email))
            throw new ResourceNotFoundException("L'email " + email + " est invalide.");
        UserAccount userAccount = repository.findByEmailOrPhone(email)
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        if (userAccount != null) {
            // Genered token
            String token = GenericUtils.generateTokenNumber();
            userAccount.setResetPasswordToken(token);
            repository.saveAndFlush(userAccount);
            // Send token activated with email
            mailService.sendForgotPasswordToken(userAccount, token);
        } else {
            throw new ResourceNotFoundException("Impossible de trouver un compte utilisateur avec l'e-mail " + email);
        }
    }

    @Override
    public void resetPassword(String token) throws ResourceNotFoundException {
        UserAccount userAccount = repository.findByResetPasswordToken(token);
        if (userAccount != null) {
            String encodedPassword = bCryptPasswordEncoder.encode("");
            userAccount.setPassword(encodedPassword);
            userAccount.setResetPasswordToken(null);
            repository.saveAndFlush(userAccount);
            // Send token activated with email
            mailService.sendResetPassword(userAccount);
        } else {
            throw new ResourceNotFoundException("Impossible de trouver un compte utilisateur avec le jeton " + token);
        }
    }

    @Override
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
                if (!repository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    user.setRole(RoleName.CUSTOMER);
                }
                break;
            case MERCHANT:
                //Verifying whether role already exists
                userDisplayName = "merchant";
                userEmail = "merchant@default.lan";
                if (!repository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    user.setRole(RoleName.MERCHANT);
                }
                break;
            case PARTNER:
                //Verifying whether role already exists
                userDisplayName = "partner";
                userEmail = "partner@default.lan";
                if (!repository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    user.setRole(RoleName.ADMIN);
                }
                break;
            default:
                //Verifying whether role already exists
                userDisplayName = "root";
                userEmail = "root@default.lan";
                if (!repository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    user.setRole(RoleName.ADMIN);
                }
                break;
        }

        if (user != null) {
            // Genered url de login
            final String loginURL = AppConstants.AuthUrl + "usingqr?email=" + user.getEmail();
            user.setLoginUrl(loginURL);
            repository.saveAndFlush(user);
        }
    }

    private UserAccount toUserRegistrationObject(OAuth2UserInfo oAuth2UserInfo, OidcIdToken idToken, OidcUserInfo userInfo) {
        return UserAccount.create(oAuth2UserInfo, idToken, userInfo);
    }

    private UserAccount updateExistingUser(UserAccount existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser = repository.findByEmailOrPhone(existingUser.getEmailOrPhone()).orElseThrow(() -> new ResourceNotFoundException("L'utilisateur est introuvable."));
        existingUser.setDisplayName(oAuth2UserInfo.getName());
        existingUser = repository.saveAndFlush(existingUser);
        return existingUser;
    }

    private SignupRequest toSignUpRequestObject(OAuth2UserInfo oAuth2UserInfo) {
        return SignupRequest.getBuilder().addLastName(oAuth2UserInfo.getName())
                .addEmail(oAuth2UserInfo.getEmail())
                .addImageUrl(oAuth2UserInfo.getImageUrl())
                .build();
    }
}