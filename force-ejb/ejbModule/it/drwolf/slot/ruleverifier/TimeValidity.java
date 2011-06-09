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
	private final String WARNING_THRESHOLD = "WarningThreshold";

	final private List<VerifierParameterDef> params = new ArrayList<VerifierParameterDef>();

	// private VerifierParameterInst earlierParameterInst;
	// private VerifierParameterInst followingParameterInst;

	public TimeValidity() {
		params.add(new VerifierParameterDef(this.EARLIER_DATE, "Earlier Date",
				DataType.DATE, false));
		params.add(new VerifierParameterDef(this.FOLLOWING_DATE,
				"Following date", DataType.DATE, false));
		params.add(new VerifierParameterDef(this.WARNING_THRESHOLD,
				"Warning Theshold", DataType.INTEGER, true));
	}

	public VerifierReport verify(Map<String, Object> params) {
		// earlierParameterInst = params.get(this.EARLIER_DATE);
		Object earlierDateObj = params.get(this.EARLIER_DATE);
		Date earlierDate = null;
		if (earlierDateObj instanceof Calendar) {
			Calendar earlierDateCalendar = (Calendar) earlierDateObj;
			earlierDate = earlierDateCalendar.getTime();
		} else if (earlierDateObj instanceof Date) {
			earlierDate = (Date) earlierDateObj;
		}

		// followingParameterInst = params.get(this.FOLLOWING_DATE);
		Object followingDateObj = params.get(this.FOLLOWING_DATE);
		Date followingDate = null;
		if (followingDateObj instanceof Calendar) {
			Calendar followingDateCalendar = (Calendar) followingDateObj;
			followingDate = followingDateCalendar.getTime();
		} else if (followingDateObj instanceof Date) {
			followingDate = (Date) followingDateObj;
		}

		VerifierReport report = new VerifierReport();
		if (followingDate.before(earlierDate)) {
			report.setResult(VerifierResult.ERROR);
			// VerifierMessage message = new VerifierMessage("\""
			// + followingParameterInst.getLable() + "\" must follow \""
			// + earlierParameterInst.getLable() + "\"!",
			// VerifierMessageType.ERROR);
			// report.setMessage(message);
		} else {
			report.setResult(VerifierResult.PASSED);
		}
		return report;
	}

	public List<VerifierParameterDef> getInParams() {
		return params;
	}

	@Override
	public String toString() {
		return "TimeValidity";
	}

	public VerifierMessage getDefaultErrorMessage() {
		// return new VerifierMessage(
		// "\"" + followingParameterInst.getLable() + "\" must follow \""
		// + earlierParameterInst.getLable() + "\"!",
		// VerifierMessageType.ERROR);
		return new VerifierMessage("Time validity rule not verified!",
				VerifierMessageType.ERROR);
	}

	public VerifierMessage getDefaultWarningMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
