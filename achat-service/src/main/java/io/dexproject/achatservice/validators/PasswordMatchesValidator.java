package io.dexproject.achatservice.validators;

import io.dexproject.achatservice.security.dto.request.SignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, SignupRequest> {

	@Override
	public boolean isValid(final SignupRequest user, final ConstraintValidatorContext context) {
		return user.getPasswordTxt().equals(user.getMatchingPassword());
	}
}
