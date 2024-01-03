package com.dexproject.shop.api.email;

import com.dexproject.shop.api.entities.Cart;
import com.dexproject.shop.api.entities.UserAccount;

public interface MailService {
	public void sendOrderEmailConfirmation(Cart cart);
	public void sendVerificationToken(UserAccount user, String token);
	public void sendQrCodeLogin(UserAccount user);
	public void sendForgotPasswordToken(UserAccount user, String token);
	public void sendResetPassword(UserAccount user);
}
