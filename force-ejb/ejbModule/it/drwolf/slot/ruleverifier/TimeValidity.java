package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

public class TimeValidity implements IRuleVerifier {

	private final String EARLIER_DATE = "EarlierDate";
	private final String FOLLOWING_DATE = "FollowingDate";
	private final String WARNING_THRESHOLD = "WarningThreshold";

	private final String DESCRIPTION = "La regola controlla che la data selezionata per il parametro \"Data Anteriore\" "
			+ "sia anteriore a quella selezionata per \"Data Posteriore\". "
			+ "Nel caso le date siano molteplici vengono confrontate in modo che tutte le Earlier siano anteriori alle Following";

	final private List<VerifierParameterDef> params = new ArrayList<VerifierParameterDef>();

	private VerifierReport verifierReport = new VerifierReport();

	public TimeValidity() {
		params.add(new VerifierParameterDef(this.EARLIER_DATE,
				"Data Anteriore", DataType.DATE, false, false));
		params.add(new VerifierParameterDef(this.FOLLOWING_DATE,
				"Data Posteriore", DataType.DATE, false, false));
		params.add(new VerifierParameterDef(this.WARNING_THRESHOLD,
				"Warning Theshold", DataType.INTEGER, true, true));
	}

	public VerifierReport verify(Map<String, List<VerifierParameterInst>> params) {
		List<VerifierParameterInst> earlierParametersInst = params
				.get(this.EARLIER_DATE);
		List<VerifierParameterInst> followingParametersInst = params
				.get(this.FOLLOWING_DATE);

		VerifierParameterInst warningThresholdParameterInst = params.get(
				this.WARNING_THRESHOLD).get(0);
		// Integer warningThreshold = (Integer)
		// warningThresholdParameterInst.getValue();

		this.verifierReport = new VerifierReport();
		this.verifierReport.setResult(VerifierResult.PASSED);

		if (!earlierParametersInst.get(0).isFallible()) {
			fromEarliers(earlierParametersInst, followingParametersInst,
					warningThresholdParameterInst);
		} else if (!followingParametersInst.get(0).isFallible()) {
			fromFollowers(earlierParametersInst, followingParametersInst,
					warningThresholdParameterInst);
		} else if (earlierParametersInst.size() == 1) {
			fromEarliers(earlierParametersInst, followingParametersInst,
					warningThresholdParameterInst);
		} else {
			fromFollowers(earlierParametersInst, followingParametersInst,
					warningThresholdParameterInst);
		}

		return this.verifierReport;
	}

	private void fromEarliers(
			List<VerifierParameterInst> earlierParametersInst,
			List<VerifierParameterInst> followingParametersInst,
			VerifierParameterInst warningThresholdParameterInst) {

		Integer threshold = (Integer) warningThresholdParameterInst.getValue();

		// trovo la soglia maggiore che deve essere rispettata
		Date earlierThreshold = null;
		for (VerifierParameterInst parameterInst : earlierParametersInst) {
			Object value = parameterInst.getValue();
			Date eDate = null;
			if (value instanceof Calendar) {
				Calendar earlierDateCalendar = (Calendar) value;
				eDate = earlierDateCalendar.getTime();
			} else if (value instanceof Date) {
				eDate = (Date) value;
			}

			if (earlierThreshold == null || eDate.after(earlierThreshold)) {
				earlierThreshold = eDate;
			}
		}

		// verifico
		for (VerifierParameterInst parameterInst : followingParametersInst) {
			Object value = parameterInst.getValue();
			Date fDate = null;
			if (value instanceof Calendar) {
				Calendar earlierDateCalendar = (Calendar) value;
				fDate = earlierDateCalendar.getTime();
			} else if (value instanceof Date) {
				fDate = (Date) value;
			}

			if (fDate.before(earlierThreshold)) {
				this.verifierReport.setResult(VerifierResult.ERROR);
				this.verifierReport.getFailedParams().add(parameterInst);
			} else if (threshold != null) {
				Date addDays = DateUtils.addDays(earlierThreshold, threshold);
				if (addDays.after(fDate)) {
					if (!this.verifierReport.getResult().equals(
							VerifierResult.ERROR)) {
						this.verifierReport.setResult(VerifierResult.WARNING);
					}
					this.verifierReport.getWarningParams().add(parameterInst);
				}
			}
		}
	}

	private void fromFollowers(
			List<VerifierParameterInst> earlierParametersInst,
			List<VerifierParameterInst> followingParametersInst,
			VerifierParameterInst warningThresholdParameterInst) {

		Integer threshold = (Integer) warningThresholdParameterInst.getValue();

		// trovo la soglia minore che deve essere rispettata
		Date followerThreshold = null;
		for (VerifierParameterInst parameterInst : followingParametersInst) {
			Object value = parameterInst.getValue();
			Date fDate = null;
			if (value instanceof Calendar) {
				Calendar earlierDateCalendar = (Calendar) value;
				fDate = earlierDateCalendar.getTime();
			} else if (value instanceof Date) {
				fDate = (Date) value;
			}

			if (followerThreshold == null || fDate.before(followerThreshold)) {
				followerThreshold = fDate;
			}
		}

		// verifico
		for (VerifierParameterInst parameterInst : earlierParametersInst) {
			Object value = parameterInst.getValue();
			Date eDate = null;
			if (value instanceof Calendar) {
				Calendar earlierDateCalendar = (Calendar) value;
				eDate = earlierDateCalendar.getTime();
			} else if (value instanceof Date) {
				eDate = (Date) value;
			}

			if (eDate.after(followerThreshold)) {
				this.verifierReport.setResult(VerifierResult.ERROR);
				this.verifierReport.getFailedParams().add(parameterInst);
			} else if (threshold != null) {
				Date addDays = DateUtils.addDays(eDate, threshold);
				if (addDays.after(followerThreshold)) {
					if (!this.verifierReport.getResult().equals(
							VerifierResult.ERROR)) {
						this.verifierReport.setResult(VerifierResult.WARNING);
					}
					this.verifierReport.getWarningParams().add(parameterInst);
				}
			}
		}
	}

	public List<VerifierParameterDef> getInParams() {
		return params;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

	public VerifierMessage getDefaultErrorMessage() {
		return new VerifierMessage("Time validity rule not verified!",
				VerifierMessageType.ERROR);
	}

	public VerifierMessage getDefaultWarningMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		return this.DESCRIPTION;
	}

}
