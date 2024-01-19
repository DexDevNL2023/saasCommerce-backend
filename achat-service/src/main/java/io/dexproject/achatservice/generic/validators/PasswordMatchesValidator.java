package io.dexproject.achatservice.generic.validators;

import io.dexproject.achatservice.generic.security.crud.dto.request.UserFormPasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserFormPasswordRequest> {

	@Override
    public boolean isValid(final UserFormPasswordRequest user, final ConstraintValidatorContext context) {
        return user.getNewPassword().equals(user.getMatchingPassword());
	}
}
