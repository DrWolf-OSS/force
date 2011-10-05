package it.drwolf.slot.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

public class Validator {

	public void validateLength(String value, Integer minLength,
			Integer maxLength) {
		if (value != null && !value.equals("")) {
			if (minLength != null) {
				if (value.length() < minLength) {
					FacesMessage message = new FacesMessage();
					message.setSummary("La stringa deve essere lunga almeno "
							+ minLength + " caratteri");
					throw new ValidatorException(message);
				}
			}

			if (maxLength != null) {
				if (value.length() > maxLength) {
					FacesMessage message = new FacesMessage();
					message.setSummary("La stringa deve essere lunga meno di "
							+ maxLength + " caratteri");
					throw new ValidatorException(message);
				}
			}
		}
	}

	public void validateMinMax(Integer value, Integer min, Integer max) {
		if (value != null) {
			if (min != null) {
				if (value < min) {
					FacesMessage message = new FacesMessage();
					message.setSummary("Il valore deve essere maggiore di "
							+ min);
					throw new ValidatorException(message);
				}
			}

			if (max != null) {
				if (value > max) {
					FacesMessage message = new FacesMessage();
					message.setSummary("Il valore deve essere minore di " + max);
					throw new ValidatorException(message);
				}
			}
		}
	}

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
				message.setSummary("Valore non valido");
				throw new ValidatorException(message);
			}
		}
	}

}
