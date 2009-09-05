/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.utils;

import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.util.ValidatorUtils;

import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.Resources;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author Hannes
 */
public class InputValidator {
    
    private static javax.sql.DataSource dataSource;
    private static java.sql.Connection connection = null;

    public static boolean validateExactLength(Object bean, ValidatorAction va, Field field, ActionMessages errors, Validator validator, HttpServletRequest request) {
        String value = null;
        if (bean instanceof String) {
            value = (String) bean;
        } else {
            value = ValidatorUtils.getValueAsString(bean, field.getProperty());
        }
        String exactLength = field.getVarValue("exactlength");
        if (GenericValidator.isBlankOrNull(value)) {
            return false;
        }
        try {
            int length = Integer.parseInt(exactLength);
            if (value != null && value.length() != length) {
                errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
                return false;
            }
        } catch (Exception e) {
            errors.add(field.getKey(), Resources.getActionMessage(request, va, field));
            return false;
        }
        return true;
    }
    
}
