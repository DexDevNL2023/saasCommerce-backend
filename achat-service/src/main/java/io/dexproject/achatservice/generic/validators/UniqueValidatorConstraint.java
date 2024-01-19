package io.dexproject.achatservice.generic.validators;

import jakarta.validation.ConstraintValidator;

public class UniqueValidatorConstraint implements ConstraintValidator<UniqueValidator, E> {
    private FieldValueExists service;
    private String fieldName;
    private final GenericRepository<E> repository;

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