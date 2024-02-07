package io.dexproject.achatservice.email;

import io.dexproject.achatservice.security.crud.entities.UserAccount;

public interface MailService {
	void sendVerificationToken(UserAccount user, String token);
	void sendQrCodeLogin(UserAccount user);
	void sendForgotPasswordToken(UserAccount user, String token);
	void sendResetPassword(UserAccount user);
}
