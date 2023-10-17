package org.itech.locator.form.webapp.validation;

import org.itech.locator.form.webapp.bean.ValueListHolder;

import java.io.IOException;
import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.itech.locator.form.webapp.validation.annotation.Included;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IncludedValidator implements ConstraintValidator<Included, String> {
    
    ValueListHolder[] valueList;
    
    @Override
    public void initialize(Included included) {
        ObjectMapper mapper = new ObjectMapper();
        ClassLoader cLoader = this.getClass().getClassLoader();
        try {
            mapper.configure(Feature.ALLOW_COMMENTS, true);
            valueList = mapper.readValue(cLoader.getResourceAsStream(included.resourcePath()), ValueListHolder[].class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isEmpty(value) || Arrays.stream(valueList).anyMatch(e -> e.getList().contains(value));
    }
    
}
