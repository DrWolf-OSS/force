package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TimeValidity implements IRuleVerifier {

	private final String EXPIRATION_DATE = "ExpirationDate";
	private final String DATE_TO_VERIFY = "DateToVerify";

	final private List<VerifierParameter> params = new ArrayList<VerifierParameter>();

	public TimeValidity() {
		params.add(new VerifierParameter(this.EXPIRATION_DATE, DataType.DATE));
		params.add(new VerifierParameter(this.DATE_TO_VERIFY, DataType.DATE));
	}

	public Boolean verify(Map<String, Object> params) {
		Date expirationDate = (Date) params.get(this.EXPIRATION_DATE);
		Date dateToVerify = (Date) params.get(this.DATE_TO_VERIFY);

		if (dateToVerify.after(expirationDate))
			return false;
		else
			return true;
	}

	public List<VerifierParameter> getInParams() {
		return params;
	}

	@Override
	public String toString() {
		return "TimeValidity";
	}

}
