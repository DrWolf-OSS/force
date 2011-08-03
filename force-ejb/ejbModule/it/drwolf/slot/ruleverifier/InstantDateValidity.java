package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InstantDateValidity implements IRuleVerifier {

	private final String DATE_TO_VERIFY = "DateToVerify";

	private final String DESCRIPTION = "La regola controlla che le date selezionate per il parametro \"Data da verificare\" "
			+ "siano anteriori a quella attuale";

	final private List<VerifierParameterDef> params = new ArrayList<VerifierParameterDef>();

	private VerifierReport verifierReport = new VerifierReport();

	public InstantDateValidity() {
		params.add(new VerifierParameterDef(this.DATE_TO_VERIFY,
				"Data da verificare", DataType.DATE, false, false, false));
	}

	public VerifierReport verify(
			Map<String, List<VerifierParameterInst>> parameterInsts) {
		List<VerifierParameterInst> datesToVerify = parameterInsts
				.get(this.DATE_TO_VERIFY);
		Date today = new Date();

		this.verifierReport = new VerifierReport();
		this.verifierReport.setResult(VerifierResult.PASSED);

		for (VerifierParameterInst parameterInst : datesToVerify) {
			Object value = parameterInst.getValue();
			Date dateToVerify = null;
			if (value instanceof Date) {
				dateToVerify = (Date) value;
			} else if (value instanceof Calendar) {
				dateToVerify = ((Calendar) value).getTime();
			}

			if (dateToVerify.before(today)) {
				verifierReport.setResult(VerifierResult.ERROR);
				verifierReport.getFailedParams().add(parameterInst);
			}
		}

		return verifierReport;
	}

	public List<VerifierParameterDef> getInParams() {
		return params;
	}

	public String getDefaultErrorMessage() {
		return "Default instant date verification error message";
	}

	public String getDefaultWarningMessage() {
		return "Default instant date verification warning message";
	}

	public String getDescription() {
		return DESCRIPTION;
	}

}
