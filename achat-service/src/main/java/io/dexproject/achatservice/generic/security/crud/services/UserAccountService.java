package io.dexproject.achatservice.generic.security.crud.services;

import io.dexproject.achatservice.generic.email.MailService;
import io.dexproject.achatservice.generic.exceptions.OAuth2AuthenticationProcessingException;
import io.dexproject.achatservice.generic.exceptions.RessourceNotFoundException;
import io.dexproject.achatservice.generic.mapper.GenericMapperUtils;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.LoginReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PagedResponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PermissionReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.UserReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.LoginRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.SignupRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormPasswordRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserRequest;
import io.dexproject.achatservice.generic.security.crud.entities.Permission;
import io.dexproject.achatservice.generic.security.crud.entities.Role;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.security.crud.entities.VerifyToken;
import io.dexproject.achatservice.generic.security.crud.enums.RoleName;
import io.dexproject.achatservice.generic.security.crud.repositories.PermissionRepository;
import io.dexproject.achatservice.generic.security.crud.repositories.RoleRepository;
import io.dexproject.achatservice.generic.security.crud.repositories.UserAccountRepository;
import io.dexproject.achatservice.generic.security.crud.repositories.VerifyTokenRepository;
import io.dexproject.achatservice.generic.security.oauth2.users.OAuth2UserInfo;
import io.dexproject.achatservice.generic.security.oauth2.users.OAuth2UserInfoFactory;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.GenericUtils;
import io.dexproject.achatservice.generic.utils.JwtUtils;
import io.dexproject.achatservice.generic.validators.log.LogExecution;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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

import java.util.*;

@Service
@Transactional
public class UserAccountService implements UserDetailsService {

    private final PasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
    private final MailService mailService;
    private final JwtUtils jwtUtils;
    private final UserAccountRepository repository;
    private  final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final VerifyTokenRepository tokenRepository;

    public UserAccountService(MailService mailService, JwtUtils jwtUtils, UserAccountRepository repository, RoleRepository roleRepository, PermissionRepository permissionRepository, VerifyTokenRepository tokenRepository) {
        this.mailService = mailService;
        this.jwtUtils = jwtUtils;
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @LogExecution
    public UserAccount registerUser(SignupRequest userForm) {
        //Verifying whether user already exists
        if (repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new RessourceNotFoundException("L'e-mail ou le téléphone est déjà utilisé!");
        // Create new user's account
        // Mapper Dto
        UserAccount newUser = GenericMapperUtils.map(userForm, UserAccount.class);
        // On verifie que la langue q ete rensegnee
        if (userForm.getLangKey() == null) {
            newUser.setLangKey("Fr"); // default language
        } else { // on verifie aue la langue est prise en co;pte dans l'application
            newUser.setLangKey(GenericUtils.verifieFormatLangue(userForm.getLangKey()));
        }
        // Genered url de login
        final String loginURL = AppConstants.AuthUrl + "usingqr?login=" + newUser.getEmailOrPhone();
        newUser.setLoginUrl(loginURL);
        newUser.setActived(newUser.isUsingQr());
        // Build user roles
        for (Long roleId : userForm.getRoles()) {
            roleRepository.findById(roleId).ifPresent(newUser::addAuthorities);
        }
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

    @Transactional
    @LogExecution
    public UserAccount processOAuthRegister(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
        if (oAuth2UserInfo.getName().isEmpty()) {
            throw new OAuth2AuthenticationProcessingException("Le nom " + oAuth2UserInfo.getName() + " introuvable auprès du fournisseur OAuth2");
        } else if (oAuth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationProcessingException("E-mail " + oAuth2UserInfo.getEmail() + " introuvable auprès du fournisseur OAuth2");
        }
        UserAccount newOAuthUser = toUserRegistration(oAuth2UserInfo, idToken, userInfo);
        if (repository.existsByEmailOrPhone(newOAuthUser.getEmail())) {
            newOAuthUser = updateExistingUser(newOAuthUser, oAuth2UserInfo);
        } else {
            SignupRequest oAuthUserForm = toSignUpRequest(oAuth2UserInfo);
            newOAuthUser = registerUser(oAuthUserForm);
        }
        return newOAuthUser;
    }

    @Transactional
    @LogExecution
    public String processOAuthLogin(UserDetails userPrincipal) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userPrincipal.getUsername()))
            throw new RessourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        UserAccount loginUser = repository.findByEmailOrPhone(userPrincipal.getUsername()).orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        // On vérifie que le compte utilisateur est activé
        if (!loginUser.isActived()) throw new RessourceNotFoundException("Le compte utilisateur n'est pas activé!");
        //This constructor can only be used by AuthenticationManager
        // générer le JWT
        String jwt = jwtUtils.generateJwtTokens(loginUser);
        // Update user's account
        loginUser.setAccesToken(jwt);
        loginUser.setConnected(true);
        loginUser = repository.saveAndFlush(loginUser);
        return loginUser.getAccesToken();
    }

    @Transactional
    @LogExecution
    public LoginReponse loginUser(LoginRequest userForm) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new RessourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        UserAccount loginUser = repository.findByEmailOrPhone(userForm.getEmailOrPhone())
                .orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        // On vérifie que le compte utilisateur est activé
        if (!loginUser.isActived()) throw new RessourceNotFoundException("Le compte utilisateur n'est pas activé!");
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
                throw new RessourceNotFoundException("Le login ou le mot de passe n'est pas correct!");
        }
        //This constructor can only be used by AuthenticationManager
        // générer le JWT
        String jwt = jwtUtils.generateJwtTokens(loginUser);
        // Update user's account
        loginUser.setAccesToken(jwt);
        loginUser.setConnected(true);
        repository.saveAndFlush(loginUser);
        // Mapper Dto
        return GenericMapperUtils.map(loginUser, LoginReponse.class);
    }

    @Transactional
    @LogExecution
    public LoginReponse loginUsingQrCode(String emailOrPhone) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(emailOrPhone))
            throw new RessourceNotFoundException("L'utilisateur n'existe pas par ce code QR!");
        UserAccount loginUser = repository.findByEmailOrPhone(emailOrPhone)
                .orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        // On vérifie que le compte utilisateur est activé
        if (!loginUser.isUsingQr())
            throw new RessourceNotFoundException("Compte utilisateur non autorisé à se connecter avec le code QR!");
        // On vérifie que le compte utilisateur est activé
        if (!loginUser.isActived()) throw new RessourceNotFoundException("Le compte utilisateur n'est pas activé!");
        //This constructor can only be used by AuthenticationManager
        // générer le JWT
        String jwt = jwtUtils.generateJwtTokens(loginUser);
        // Update user's account
        loginUser.setAccesToken(jwt);
        loginUser.setConnected(true);
        repository.saveAndFlush(loginUser);
        // Mapper Dto
        return GenericMapperUtils.map(loginUser, LoginReponse.class);
    }

    @Transactional
    @LogExecution
    public void logoutUser(LoginRequest userForm) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new RessourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        UserAccount logoutUser = repository.findByEmailOrPhone(userForm.getEmailOrPhone())
                .orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        // Update user's account
        logoutUser.setAccesToken("");
        logoutUser.setConnected(false);
        repository.saveAndFlush(logoutUser);
        SecurityContextHolder.clearContext();
    }

    @Transactional
    @LogExecution
    public UserReponse createUser(UserRequest userForm) {
        //Verifying whether user already exists
        if (repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new RessourceNotFoundException("L'e-mail ou le téléphone est déjà utilisé!");
        // Create new user's account
        // Mapper Dto
        UserAccount newUser = GenericMapperUtils.map(userForm, UserAccount.class);
        // On verifie que la langue q ete rensegnee
        if (userForm.getLangKey() == null) {
            newUser.setLangKey("Fr"); // default language
        } else { // on verifie aue la langue est prise en co;pte dans l'application
            newUser.setLangKey(GenericUtils.verifieFormatLangue(userForm.getLangKey()));
        }
        // Genered url de login
        final String loginURL = AppConstants.AuthUrl + "usingqr?login=" + newUser.getEmailOrPhone();
        newUser.setLoginUrl(loginURL);
        newUser.setActived(newUser.isUsingQr());
        newUser.setPassword(bCryptPasswordEncoder.encode(""));
        // Build user roles
        for (Long roleId : userForm.getRoles()) {
            Role authority = roleRepository.findById(roleId).orElse(null);
            if (authority != null) {
                newUser.addAuthorities(authority);
            }
        }
        // Create user's account
        newUser = repository.saveAndFlush(newUser);
        // Mapper Dto
        return GenericMapperUtils.map(newUser, UserReponse.class);
    }

    @Transactional
    @LogExecution
    public UserReponse editUser(UserRequest userForm) {
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new RessourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        // Create new user's account
        // Mapper Dto
        UserAccount updatedUser = GenericMapperUtils.map(userForm, UserAccount.class);
        // On verifie que la langue q ete rensegnee
        if (userForm.getLangKey() == null) {
            updatedUser.setLangKey("Fr"); // default language
        } else { // on verifie aue la langue est prise en co;pte dans l'application
            updatedUser.setLangKey(GenericUtils.verifieFormatLangue(userForm.getLangKey()));
        }
        // Genered url de login
        final String loginURL = AppConstants.AuthUrl + "usingqr?login=" + updatedUser.getEmailOrPhone();
        updatedUser.setLoginUrl(loginURL);
        updatedUser.setActived(updatedUser.isUsingQr());
        UserAccount findUser = repository.findByEmailOrPhone(userForm.getEmailOrPhone())
                .orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        updatedUser.setId(findUser.getId());
        // Build user roles
        for (Long roleId : userForm.getRoles()) {
            Role authority = roleRepository.findById(roleId).orElse(null);
            if (authority != null) {
                updatedUser.addAuthorities(authority);
            }
        }
        // Create user's account
        updatedUser = repository.saveAndFlush(updatedUser);
        // Mapper Dto
        return GenericMapperUtils.map(updatedUser, UserReponse.class);
    }

    @Transactional
    @LogExecution
    public UserReponse editPassword(UserFormPasswordRequest userForm) {
        if (!userForm.getNewPassword().equals(userForm.getMatchingPassword()))
            throw new RessourceNotFoundException("Veuillez confirmer votre mot de passe!");
        //Verifying whether user already exists
        if (!repository.existsByEmailOrPhone(userForm.getEmailOrPhone()))
            throw new RessourceNotFoundException("L'utilisateur n'existe pas par cet e-mail ou ce téléphone!");
        UserAccount updatedUser = repository.findByEmailOrPhone(userForm.getEmailOrPhone())
                .orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        String lastPass = bCryptPasswordEncoder.encode(userForm.getLastPassword());
        if (!lastPass.equals(updatedUser.getPassword()))
            throw new RessourceNotFoundException("Veuillez confirmer votre dernier mot de passe!");
        // Update user's account with new password
        updatedUser.setPassword(bCryptPasswordEncoder.encode(userForm.getNewPassword()));
        updatedUser = repository.saveAndFlush(updatedUser);
        // Mapper Dto
        return GenericMapperUtils.map(updatedUser, UserReponse.class);
    }

    @Transactional
    @LogExecution
    public UserReponse suspendUserById(Long id) {
        //Verifying whether user already exists
        if (!repository.existsById(id))
            throw new RessourceNotFoundException("L'utilisateur n'existe pas avec cet identifiant " + id);
        UserAccount updatedUser = repository.findById(id).orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        // Update user's account status
        updatedUser.setActived(false);
        updatedUser = repository.saveAndFlush(updatedUser);
        // Mapper Dto
        return GenericMapperUtils.map(updatedUser, UserReponse.class);
    }

    @Transactional
    @LogExecution
    public void deleteUserById(Long id) {
        //Verifying whether user already exists
        if (!repository.existsById(id))
            throw new RessourceNotFoundException("L'utilisateur n'existe pas avec cet identifiant " + id);
        repository.deleteById(id);
    }

    @Transactional
    @LogExecution
    public UserReponse findUserById(Long id) {
        UserAccount userAccount = repository.findById(id).orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        // Mapper Dto
        return GenericMapperUtils.map(userAccount, UserReponse.class);
    }

    @Transactional
    @LogExecution
    public UserAccount loadCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new RessourceNotFoundException("Impossible de retouver l'utilisateur courant!");
        }
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return repository.findByEmailOrPhone(userPrincipal.getUsername())
                .orElseThrow(() -> new RessourceNotFoundException("Aucun utilisateur n'existe avec le nom utilisateur " + userPrincipal.getUsername()));
    }

    @Transactional
    @LogExecution
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        UserAccount userAccount = repository.findByEmailOrPhone(emailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException("Nom utilisateur invalide"));
        Collection<? extends GrantedAuthority> authorities = GenericUtils.buildSimpleGrantedAuthorities(userAccount.getRoles());
        return new User(userAccount.getEmailOrPhone(), userAccount.getPassword(), authorities);
    }

    @Transactional
    @LogExecution
    public UserAccount findByUsername(String emailOrPhone) throws UsernameNotFoundException {
        return repository.findByEmailOrPhone(emailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException("Nom utilisateur invalide"));
    }

    @Transactional
    @LogExecution
    public List<UserReponse> getAllUsers() {
        return GenericMapperUtils.mapAll(repository.findAll(), UserReponse.class);
    }

    @Transactional
    @LogExecution
    public List<UserReponse> getAllUsersByRole(RoleName roleName) {
        return GenericMapperUtils.mapAll(repository.findAllByRolename(roleName), UserReponse.class);
    }

    @Transactional
    @LogExecution
    public PagedResponse<UserReponse> getAllUsersByPage(Integer page, Integer size) {
        // Vérifier la syntaxe de page et size
        GenericUtils.validatePageNumberAndSize(page, size);
        // Construire la pagination
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, AppConstants.PERIODE_FILTABLE_FIELD);
        Page<UserAccount> allUsers = repository.findAll(pageable);
        if (allUsers.getNumberOfElements() == 0)
            throw new RessourceNotFoundException("La liste de recherche d'utilisateurs est vide!");
        List<UserAccount> list = allUsers.stream().toList();
        // Mapper Dto
        return new PagedResponse<>(GenericMapperUtils.mapAll(list, UserReponse.class), allUsers.getNumber(), allUsers.getSize(), allUsers.getTotalElements(), allUsers.getTotalPages(), allUsers.isLast());
    }

    @Transactional
    @LogExecution
    public List<UserReponse> search(String motCle) {
        return GenericMapperUtils.mapAll(repository.search(motCle), UserReponse.class);
    }

    @Transactional
    @LogExecution
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

    @Transactional
    @LogExecution
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

    @Transactional
    @LogExecution
    public void forgotPassword(String email) throws RessourceNotFoundException {
        if (!GenericUtils.isValidEmailAddress(email))
            throw new RessourceNotFoundException("L'email " + email + " est invalide.");
        UserAccount userAccount = repository.findByEmailOrPhone(email)
                .orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        if (userAccount != null) {
            // Genered token
            String token = GenericUtils.generateTokenNumber();
            userAccount.setResetPasswordToken(token);
            repository.saveAndFlush(userAccount);
            // Send token activated with email
            mailService.sendForgotPasswordToken(userAccount, token);
        } else {
            throw new RessourceNotFoundException("Impossible de trouver un compte utilisateur avec l'e-mail " + email);
        }
    }

    @Transactional
    @LogExecution
    public void resetPassword(String token) throws RessourceNotFoundException {
        UserAccount userAccount = repository.findByResetPasswordToken(token);
        if (userAccount != null) {
            String encodedPassword = bCryptPasswordEncoder.encode("");
            userAccount.setPassword(encodedPassword);
            userAccount.setResetPasswordToken(null);
            repository.saveAndFlush(userAccount);
            // Send token activated with email
            mailService.sendResetPassword(userAccount);
        } else {
            throw new RessourceNotFoundException("Impossible de trouver un compte utilisateur avec le jeton " + token);
        }
    }

    @Transactional
    @LogExecution
    public void addDefaultUsers(RoleName roleCle) {
        UserAccount user = null;
        String userEmail;
        String userDisplayName;
        RoleName roleName = null;

        // On initialise l'utlisateurroot
        switch (roleCle) {
            case CUSTOMER:
                //Verifying whether role already exists
                userDisplayName = "customer";
                userEmail = "customer@default.lan";
                if (!repository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    roleName = RoleName.CUSTOMER;
                }
                break;
            case MERCHANT:
                //Verifying whether role already exists
                userDisplayName = "merchant";
                userEmail = "merchant@default.lan";
                if (!repository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    roleName = RoleName.MERCHANT;
                }
                break;
            case PARTNER:
                //Verifying whether role already exists
                userDisplayName = "partner";
                userEmail = "partner@default.lan";
                if (!repository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    roleName = RoleName.PARTNER;
                }
                break;
            default:
                //Verifying whether role already exists
                userDisplayName = "root";
                userEmail = "root@default.lan";
                if (!repository.existsByEmailOrPhone(userEmail)) {
                    user = new UserAccount(userDisplayName, userEmail, bCryptPasswordEncoder.encode(""), "656668310", "Yaoundé, Cameroun", true);
                    roleName = RoleName.ADMIN;
                }
                break;
        }

        if (user != null) {
            Role authority = roleRepository.findByRoleName(roleName);
            if (authority == null) {
                authority = new Role();
                authority.setIsSuper(roleName.equals(RoleName.ADMIN));
                authority.setLibelle(roleName);
                authority = roleRepository.save(authority);
            }
            user.addAuthorities(authority);
            // Genered url de login
            final String loginURL = AppConstants.AuthUrl + "usingqr?email=" + user.getEmail();
            user.setLoginUrl(loginURL);
            repository.saveAndFlush(user);
        }
    }

    @Transactional
    @LogExecution
    public List<PermissionReponse> getAutorisations(Long userId) {
        List<Permission> permissions = new ArrayList<>();
        UserAccount user = repository.findById(userId).orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        for (Role role : user.getRoles()) {
            permissions.addAll(permissionRepository.findAllByRole(role));
        }
        // Mapper Dto
        return GenericMapperUtils.mapAll(permissions, PermissionReponse.class);
    }

    @Transactional
    @LogExecution
    private UserAccount toUserRegistration(OAuth2UserInfo oAuth2UserInfo, OidcIdToken idToken, OidcUserInfo userInfo) {
        return UserAccount.create(oAuth2UserInfo, idToken, userInfo);
    }

    @Transactional
    @LogExecution
    private UserAccount updateExistingUser(UserAccount existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser = repository.findByEmailOrPhone(existingUser.getEmailOrPhone()).orElseThrow(() -> new RessourceNotFoundException("L'utilisateur est introuvable."));
        existingUser.setDisplayName(oAuth2UserInfo.getName());
        existingUser = repository.saveAndFlush(existingUser);
        return existingUser;
    }

    @Transactional
    @LogExecution
    private SignupRequest toSignUpRequest(OAuth2UserInfo oAuth2UserInfo) {
        return SignupRequest.getBuilder().addLastName(oAuth2UserInfo.getName())
                .addEmail(oAuth2UserInfo.getEmail())
                .addImageUrl(oAuth2UserInfo.getImageUrl())
                .build();
    }
}