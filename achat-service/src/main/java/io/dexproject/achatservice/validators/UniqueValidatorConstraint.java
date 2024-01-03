package io.dexproject.achatservice.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueValidatorConstraint implements ConstraintValidator<UniqueValidator, Object> {
    private FieldValueExists service;
    private String fieldName;

    @Override
    public void initialize(UniqueValidator unique) {
        Class<? extends FieldValueExists> clazz = unique.service();
        this.fieldName = unique.fieldName();
        String serviceQualifier = unique.serviceQualifier();

        if (!serviceQualifier.equals("")) {
            this.service = (FieldValueExists) ApplicationContextProvider.getBean(serviceQualifier, clazz);
        } else {
            this.service = (FieldValueExists) ApplicationContextProvider.getBean(clazz);
        }
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return !this.service.fieldValueExists(o, this.fieldName);
    }
}