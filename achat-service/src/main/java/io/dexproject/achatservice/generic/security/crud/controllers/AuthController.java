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

    private final UserAccountService userService;

    public AuthController(UserAccountService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResourceResponse> loginUser(@NotEmpty @Valid @RequestBody LoginRequest loginRequest) {
        LoginReponse loginDto = userService.loginUser(loginRequest);
        return new ResponseEntity<>(new ResourceResponse("User login successfully!", loginDto), HttpStatus.OK);
    }

    @PostMapping("/usingqr/{emailOrPhone}")
    public ResponseEntity<ResourceResponse> verifyCode(@NotEmpty @Valid @RequestParam(value = "emailOrPhone") String emailOrPhone) {
        LoginReponse loginDto = userService.loginUsingQrCode(emailOrPhone);
        return new ResponseEntity<>(new ResourceResponse("User login successfully!", loginDto), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ResourceResponse> registerUser(@NotEmpty @Valid @RequestBody SignupRequest signUpRequest) {
        userService.registerUser(signUpRequest);
        return new ResponseEntity<>(new ResourceResponse("User registered successfully!"), HttpStatus.OK);
    }

    @PostMapping("/token/verify/{token}")
    public ResponseEntity<ResourceResponse> confirmRegistration(@NotEmpty @Valid @RequestParam(value = "token") String token) {
        String result = userService.validateVerificationToken(token);
        return new ResponseEntity<>(new ResourceResponse("Verification user registration successfully!", result), HttpStatus.OK);
    }

    @PostMapping("/token/resend/{token}")
    @ResponseBody
    public ResponseEntity<ResourceResponse> resendRegistrationToken(@NotEmpty @Valid @RequestParam(value = "token") String token) {
        if (!userService.resendVerificationToken(token)) {
            return new ResponseEntity<>(new ResourceResponse("Token not found!!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResourceResponse("Resend registration token successfully!"), HttpStatus.OK);
    }

    @PostMapping("/password/forgot/{emailOrPhone}")
    public ResponseEntity<ResourceResponse> forgotPassword(@NotEmpty @Valid @RequestParam(value = "emailOrPhone") String emailOrPhone) {
        userService.forgotPassword(emailOrPhone);
        return new ResponseEntity<>(new ResourceResponse("Forgot password token successfully!"), HttpStatus.OK);
    }

    @PostMapping("/password/reset/{token}")
    public ResponseEntity<ResourceResponse> resetPassword(@NotEmpty @Valid @RequestParam(value = "token") String token) {
        userService.resetPassword(token);
        return new ResponseEntity<>(new ResourceResponse("Reset password successfully!"), HttpStatus.OK);
    }
}