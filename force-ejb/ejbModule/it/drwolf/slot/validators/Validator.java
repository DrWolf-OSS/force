package it.drwolf.slot.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

public class Validator {

	public void validateRegex(String value, String regex, Boolean requiresMatch) {
		if (value != null && regex != null && !value.equals("")
				&& !regex.equals("")) {
			if (requiresMatch == null) {
				requiresMatch = Boolean.TRUE;
			}
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(value);
			if ((matcher.matches() ^ requiresMatch)) {
				FacesMessage message = new FacesMessage();
				message.setSummary("Valore non valido!");
				throw new ValidatorException(message);
			}
		}
	}

}
