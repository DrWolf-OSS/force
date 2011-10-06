package it.drwolf.slot.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

////IMPORTANTE!!!! (Soprattutto se in un ModalPanel)
// Mettere sempre la SEVERITY nei messaggi, altrimenti dopo un errore di validazione, 
// anche se il messaggio viene mostrato, i valori nel form vengono eliminati! 
public class Validator {

	public void validateLength(String value, Integer minLength,
			Integer maxLength) {
		if (value != null && !value.equals("")) {
			if (minLength != null) {
				if (value.length() < minLength) {
					FacesMessage message = new FacesMessage();
					message.setSummary("La stringa deve essere lunga almeno "
							+ minLength + " caratteri");
					// IMPORTANTE!!!!
					// SENZA LA SEVERITY DOPO UN ERRORE VENGONO CANCELLATI TUTTI
					// I VALORI NEL FORM
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
					//
					throw new ValidatorException(message);
				}
			}

			if (maxLength != null) {
				if (value.length() > maxLength) {
					FacesMessage message = new FacesMessage();
					message.setSummary("La stringa deve essere lunga meno di "
							+ maxLength + " caratteri");
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
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
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException(message);
				}
			}

			if (max != null) {
				if (value > max) {
					FacesMessage message = new FacesMessage();
					message.setSummary("Il valore deve essere minore di " + max);
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
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
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(message);
			}
		}
	}

}
