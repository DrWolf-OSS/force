package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TimeValidity implements IRuleVerifier {

	private final String EARLIER_DATE = "EarlierDate";
	private final String FOLLOWING_DATE = "FollowingDate";

	final private List<VerifierParameter> params = new ArrayList<VerifierParameter>();

	public TimeValidity() {
		params.add(new VerifierParameter(this.EARLIER_DATE, "Earlier Date",
				DataType.DATE));
		params.add(new VerifierParameter(this.FOLLOWING_DATE, "Following date",
				DataType.DATE));
	}

	public Boolean verify(Map<String, Object> params) {
		Object earlierDateObj = params.get(this.EARLIER_DATE);
		Date earlierDate = null;
		if (earlierDateObj instanceof Calendar) {
			Calendar earlierDateCalendar = (Calendar) earlierDateObj;
			earlierDate = earlierDateCalendar.getTime();
		} else if (earlierDateObj instanceof Date) {
			earlierDate = (Date) earlierDateObj;
		}

		Object followingDateObj = params.get(this.FOLLOWING_DATE);
		Date followingDate = null;
		if (followingDateObj instanceof Calendar) {
			Calendar followingDateCalendar = (Calendar) followingDateObj;
			followingDate = followingDateCalendar.getTime();
		} else if (followingDateObj instanceof Date) {
			followingDate = (Date) followingDateObj;
		}

		if (followingDate.before(earlierDate))
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
