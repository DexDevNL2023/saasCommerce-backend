package io.dexproject.achatservice.generic.security.crud.controllers;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserAccountService userService;

    @PostMapping("/login")
    public ResponseEntity<ResourceResponse> loginUser(@NotEmpty @RequestBody LoginRequest loginRequest) {
        LoginDto loginDto = userService.loginUser(loginRequest);
        return new ResponseEntity<>(new ResourceResponse("User login successfully!", loginDto), HttpStatus.OK);
    }

    @PostMapping("/usingqr")
    public ResponseEntity<ResourceResponse> verifyCode(@NotEmpty @RequestParam(value = "email") String email) {
        LoginDto loginDto = userService.loginUsingQrCode(email);
        return new ResponseEntity<>(new ResourceResponse("User login successfully!", loginDto), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ResourceResponse> registerUser(@NotEmpty @RequestBody SignupRequest signUpRequest) {
        userService.registerUser(signUpRequest);
        return new ResponseEntity<>(new ResourceResponse("User registered successfully!"), HttpStatus.OK);
    }

    @PostMapping("/token/verify")
    public ResponseEntity<ResourceResponse> confirmRegistration(@NotEmpty @RequestParam(value = "token") String token) {
        String result = userService.validateVerificationToken(token);
        return new ResponseEntity<>(new ResourceResponse("Verification user registration successfully!", result), HttpStatus.OK);
    }

    @PostMapping("/token/resend")
    @ResponseBody
    public ResponseEntity<ResourceResponse> resendRegistrationToken(@NotEmpty @RequestBody String expiredToken) {
        if (!userService.resendVerificationToken(expiredToken)) {
            return new ResponseEntity<>(new ResourceResponse("Token not found!!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResourceResponse("Resend registration token successfully!"), HttpStatus.OK);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<ResourceResponse> forgotPassword(@NotEmpty @RequestParam(value = "email") String email) {
        userService.forgotPassword(email);
        return new ResponseEntity<>(new ResourceResponse("Forgot password token successfully!"), HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ResourceResponse> resetPassword(@NotEmpty @RequestParam(value = "token") String token) {
        userService.resetPassword(token);
        return new ResponseEntity<>(new ResourceResponse("Reset password successfully!"), HttpStatus.OK);
    }
}