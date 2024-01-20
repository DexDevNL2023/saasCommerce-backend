package io.dexproject.achatservice.generic.email;

import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;

public interface MailService {
	void sendVerificationToken(UserAccount user, String token);
	void sendQrCodeLogin(UserAccount user);
	void sendForgotPasswordToken(UserAccount user, String token);
	void sendResetPassword(UserAccount user);
}
