package io.dexproject.achatservice.generic.security.crud.services;

import io.dexproject.achatservice.generic.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.LoginReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.PagedResponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.UserReponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.LoginRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.SignupRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormPasswordRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormRequest;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import io.dexproject.achatservice.generic.service.ServiceGeneric;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.List;
import java.util.Map;

public interface UserAccountService extends ServiceGeneric<UserFormRequest, UserReponse, UserAccount> {
    UserAccount registerUser(SignupRequest userForm);

    UserAccount processOAuthRegister(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo);

    String processOAuthLogin(UserDetails userPrincipal);

    LoginReponse loginUser(LoginRequest userForm);

    LoginReponse loginUsingQrCode(String emailOrPhone);

    void logoutUser(LoginRequest userForm);

    UserReponse createUser(UserFormRequest userForm);

    UserReponse editUser(UserFormRequest userForm);

    UserReponse editPassword(UserFormPasswordRequest userForm);

    UserReponse suspendUserById(Long id);

    void deleteUserById(Long id);

    UserReponse findUserById(Long id);

    UserAccount loadCurrentUser();

    UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException;

    List<UserReponse> getAllUsers();

    PagedResponse<UserReponse> getUsersByPage(Integer page, Integer size);

    List<UserReponse> search(String motCle);

    Boolean resendVerificationToken(String existingVerificationToken);

    String validateVerificationToken(String token);

    void forgotPassword(String email) throws ResourceNotFoundException;

    void resetPassword(String token) throws ResourceNotFoundException;

    void addDefaultUsers(RoleName roleCle);
}
