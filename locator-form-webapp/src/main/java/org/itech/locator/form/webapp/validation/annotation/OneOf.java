package org.itech.locator.form.webapp.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.itech.locator.form.webapp.validation.OneOfValidator;

@Documented
@Constraint(validatedBy = OneOfValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OneOf {
	String message() default "Invalid value";

	Class<?>[] groups() default {};

	String resourcePath() default "";

	Class<? extends Payload>[] payload() default {};

}
