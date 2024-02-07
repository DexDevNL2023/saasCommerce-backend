package io.dexproject.achatservice.validators.password;

import io.dexproject.achatservice.security.crud.dto.request.UserFormPasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserFormPasswordRequest> {

	@Override
    public boolean isValid(final UserFormPasswordRequest user, final ConstraintValidatorContext context) {
        return user.getNewPassword().equals(user.getMatchingPassword());
	}
}
