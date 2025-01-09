package com.kaiasia.app.service.utils;

import com.kaiasia.app.core.model.ApiError;
import com.kaiasia.app.core.utils.GetErrorUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidatorUtils {

    public static <T> ApiError validate(T Class, GetErrorUtils getErrorUtils) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(Class);

        for (ConstraintViolation<T> constraintViolation : constraintViolations) {
            if (constraintViolation.getMessage() != null) {
                return getErrorUtils.getError("300", new String[]{ constraintViolation.getMessage()});
            }

        }
        return new ApiError(ApiError.OK_CODE, ApiError.OK_DESC);
    }

}
