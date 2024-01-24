package io.dexproject.achatservice.generic.security.crud.controllers;

import io.dexproject.achatservice.generic.security.crud.dto.reponse.LoginReponse;
import io.dexproject.achatservice.generic.security.crud.dto.reponse.ResourceResponse;
import io.dexproject.achatservice.generic.security.crud.dto.request.LoginRequest;
import io.dexproject.achatservice.generic.security.crud.dto.request.SignupRequest;
import io.dexproject.achatservice.generic.security.crud.services.UserAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RefreshScope
public class AuthController {

    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResourceResponse> loginUser(@NotEmpty @Valid @RequestBody LoginRequest loginRequest) {
        LoginReponse loginDto = userAccountService.loginUser(loginRequest);
        return new ResponseEntity<>(new ResourceResponse("L'utilisateur s'est connecté avec succès!", loginDto), HttpStatus.OK);
    }

    @PostMapping("/usingqr/{emailOrPhone}")
    public ResponseEntity<ResourceResponse> verifyCode(@NotEmpty @Valid @RequestParam(value = "emailOrPhone") String emailOrPhone) {
        LoginReponse loginDto = userAccountService.loginUsingQrCode(emailOrPhone);
        return new ResponseEntity<>(new ResourceResponse("L'utilisateur s'est connecté avec succès!", loginDto), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ResourceResponse> registerUser(@NotEmpty @Valid @RequestBody SignupRequest signUpRequest) {
        userAccountService.registerUser(signUpRequest);
        return new ResponseEntity<>(new ResourceResponse("Utilisateur enregistré avec succès!"), HttpStatus.OK);
    }

    @PostMapping("/token/verify/{token}")
    public ResponseEntity<ResourceResponse> confirmRegistration(@NotEmpty @Valid @RequestParam(value = "token") String token) {
        String result = userAccountService.validateVerificationToken(token);
        return new ResponseEntity<>(new ResourceResponse("Vérification de l'enregistrement avec succès!", result), HttpStatus.OK);
    }

    @PostMapping("/token/resend/{token}")
    @ResponseBody
    public ResponseEntity<ResourceResponse> resendRegistrationToken(@NotEmpty @Valid @RequestParam(value = "token") String token) {
        if (!userAccountService.resendVerificationToken(token)) {
            return new ResponseEntity<>(new ResourceResponse("Jeton introuvable!!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResourceResponse("Renvoyer le jeton d'enregistrement avec succès!"), HttpStatus.OK);
    }

    @PostMapping("/password/forgot/{email}")
    public ResponseEntity<ResourceResponse> forgotPassword(@NotEmpty @Valid @RequestParam(value = "email") String email) {
        userAccountService.forgotPassword(email);
        return new ResponseEntity<>(new ResourceResponse("Jeton de mot de passe oublié avec succès!"), HttpStatus.OK);
    }

    @PostMapping("/password/reset/{token}")
    public ResponseEntity<ResourceResponse> resetPassword(@NotEmpty @Valid @RequestParam(value = "token") String token) {
        userAccountService.resetPassword(token);
        return new ResponseEntity<>(new ResourceResponse("Réinitialiser le mot de passe avec succès!"), HttpStatus.OK);
    }
}