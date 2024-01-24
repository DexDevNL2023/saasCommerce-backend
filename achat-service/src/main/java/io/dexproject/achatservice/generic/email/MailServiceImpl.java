package io.dexproject.achatservice.generic.email;

import com.google.zxing.WriterException;
import freemarker.template.Configuration;
import io.dexproject.achatservice.generic.config.MessageService;
import io.dexproject.achatservice.generic.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.utils.AppConstants;
import io.dexproject.achatservice.generic.utils.QRCodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class MailServiceImpl implements MailService {

	private final JavaMailSender mailSender;

	private final Configuration freemarkerConfiguration;

	private final MessageService messageService;

	public MailServiceImpl(JavaMailSender mailSender, Configuration freemarkerConfiguration, MessageService messageService) {
		this.mailSender = mailSender;
		this.freemarkerConfiguration = freemarkerConfiguration;
		this.messageService = messageService;
	}

	@Async
	@Override
	public void sendVerificationToken(UserAccount user, String token) {
		final String verifyURL = AppConstants.AuthUrl + "token/verify?token=" + token;
		final String message = messageService.getMessage("message.mail.verification");
	    message.replace("[[name]]", user.getDisplayName());
	    message.replace("[[URL]]", verifyURL);
	    message.replace("[[company]]", AppConstants.COMPANY_NAME);
		sendHtmlEmail(user, "Registration Confirmation", message);
	}
	
	@Async
	@Override
	public void sendQrCodeLogin(UserAccount user) {
        try {
	        byte[] qrCodeImage = new byte[0];
            // Generate and Return Qr Code in Byte Array
	        qrCodeImage = QRCodeGenerator.getQRCodeImage(user.getLoginUrl(),250,250);
            // Convert Byte Array into Base64 Encode String
            String qrcode = Base64.getEncoder().encodeToString(qrCodeImage);
    		final String message = messageService.getMessage("message.mail.qr.login");
    	    message.replace("[[name]]", user.getDisplayName());
    	    message.replace("[[URL]]", qrcode);
    	    message.replace("[[company]]", AppConstants.COMPANY_NAME);
    		sendHtmlEmail(user, "Registration Confirmation", message);
        } catch (WriterException | IOException e) {
        	throw new ResourceNotFoundException("Unable to generate QR code!");
        }
	}

	@Async
	@Override
	public void sendForgotPasswordToken(UserAccount user, String token) {
		final String verifyURL = AppConstants.AuthUrl + "password/reset?token=" + token;
		final String message = messageService.getMessage("message.mail.password.forgot");
	    message.replace("[[name]]", user.getDisplayName());
	    message.replace("[[URL]]", verifyURL);
	    message.replace("[[company]]", AppConstants.COMPANY_NAME);
		sendHtmlEmail(user, "Reinitialization Password Confirmation", message);
	}

	@Async
	@Override
	public void sendResetPassword(UserAccount user) {
		final String message = messageService.getMessage("message.mail.password.reset");
	    message.replace("[[name]]", user.getDisplayName());
	    message.replace("[[company]]", AppConstants.COMPANY_NAME);
		sendHtmlEmail(user, "Reset Password Confirmation", message);
	}

	private String geFreeMarkerTemplateContent(Map<String, Object> model, String templateName) {
		StringBuffer content = new StringBuffer();
		try {
			content.append(FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(templateName), model));
			return content.toString();
		} catch (Exception e) {
			System.out.println("Exception occured while processing fmtemplate:" + e.getMessage());
		}
		return "";
	}

	private void sendHtmlEmail(UserAccount user, String subject, String msg) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("name", user.getDisplayName());
		model.put("msg", msg);
		model.put("title", subject);
		model.put("company", AppConstants.COMPANY_NAME);
		try {
			sendHtmlMail(AppConstants.SUPPORT_EMAIL, user.getEmail(), subject, geFreeMarkerTemplateContent(model, "mail/confirmationpage.ftl"));
		} catch (MessagingException e) {
			System.out.println("Failed to send mail");
		}
	}

	public void sendHtmlMail(String from, String to, String subject, String body) throws MessagingException {
		MimeMessage mail = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
		helper.setFrom(from);
		if (to.contains(",")) {
			helper.setTo(to.split(","));
		} else {
			helper.setTo(to);
		}
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(mail);
		System.out.println("Sent mail: " + subject);
	}
}
