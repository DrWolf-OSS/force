package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TimeValidity implements IRuleVerifier {

	private final String EXPIRATION_DATE = "ExpirationDate";
	private final String DATE_TO_VERIFY = "DateToVerify";

	final private List<VerifierParameter> params = new ArrayList<VerifierParameter>();

	public TimeValidity() {
		params.add(new VerifierParameter(this.EXPIRATION_DATE,
				"Expiration Date", DataType.DATE));
		params.add(new VerifierParameter(this.DATE_TO_VERIFY, "Date To Verify",
				DataType.DATE));
	}

	public Boolean verify(Map<String, Object> params) {
		Object expirationDateObj = params.get(this.EXPIRATION_DATE);
		Date expirationDate = null;
		if (expirationDateObj instanceof Calendar) {
			Calendar expirationDateCalendar = (Calendar) expirationDateObj;
			expirationDate = expirationDateCalendar.getTime();
		} else if (expirationDateObj instanceof Date) {
			expirationDate = (Date) expirationDateObj;
		}

		Object dateToVerifyObj = params.get(this.DATE_TO_VERIFY);
		Date dateToVerify = null;
		if (dateToVerifyObj instanceof Calendar) {
			Calendar dateToVerifyCalendar = (Calendar) dateToVerifyObj;
			dateToVerify = dateToVerifyCalendar.getTime();
		} else if (expirationDateObj instanceof Date) {
			dateToVerify = (Date) dateToVerifyObj;
		}

		if (dateToVerify.before(expirationDate))
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
