package io.dexproject.achatservice.generic.email;

import com.dexproject.shop.api.entities.UserAccount;

public interface MailService {
	void sendVerificationToken(UserAccount user, String token);
	void sendQrCodeLogin(UserAccount user);
	void sendForgotPasswordToken(UserAccount user, String token);
	void sendResetPassword(UserAccount user);
}
