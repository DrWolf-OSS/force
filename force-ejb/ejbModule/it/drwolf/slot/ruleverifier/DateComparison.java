package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.entity.Dictionary;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.exceptions.WrongDataTypeException;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.time.DateUtils;

public class DateComparison implements IRuleVerifier {

	public static final String EARLIER_DATE = "EarlierDate";
	public static final String FOLLOWING_DATE = "FollowingDate";
	public static final String WARNING_THRESHOLD = "WarningThreshold";

	private final String DESCRIPTION = "La regola controlla che la data selezionata per il parametro \"Data Anteriore\" "
			+ "sia anteriore a quella selezionata per \"Data Posteriore\". "
			+ "Nel caso le date siano molteplici vengono confrontate in modo che tutte le \"Date Anteriori\" siano anteriori alle \"Date Posteriori\"";

	final private List<VerifierParameterDef> params = new ArrayList<VerifierParameterDef>();

	private VerifierReport verifierReport = new VerifierReport();

	private List<VerifierParameterInst> wrongDataTypeParameters = new ArrayList<VerifierParameterInst>();

	public DateComparison() {
		this.params.add(new VerifierParameterDef(DateComparison.EARLIER_DATE,
				"Data Anteriore", DataType.DATE, false, false, false));
		this.params.add(new VerifierParameterDef(DateComparison.FOLLOWING_DATE,
				"Data Posteriore", DataType.DATE, false, false, false));
		this.params.add(new VerifierParameterDef(
				DateComparison.WARNING_THRESHOLD,
				"Giorni prima di notifica di avvertimento", DataType.INTEGER,
				true, true, false));
	}

	// Metodi di test per recuperare un Dictionary da associare
	@SuppressWarnings("unchecked")
	private Dictionary findDateListFromDB() {
		EntityManager entityManager = (EntityManager) org.jboss.seam.Component
				.getInstance("entityManager");
		List<Dictionary> resultList = entityManager
				.createQuery("from Dictionary d where d.name=:name")
				.setParameter("name", "dizionario di date 2").getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Dictionary findDateListFromModel() {
		CustomModelController customModelController = (CustomModelController) org.jboss.seam.Component
				.getInstance("customModelController");
		return customModelController.makeDictionaryFromConstraint(
				"slot:DATEList", DataType.DATE);
	}

	private void fromEarliers(
			List<VerifierParameterInst> earlierParametersInst,
			List<VerifierParameterInst> followingParametersInst,
			VerifierParameterInst warningThresholdParameterInst)
			throws WrongDataTypeException {

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
			} else {
				throw new WrongDataTypeException(parameterInst);
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
			} else {
				throw new WrongDataTypeException(parameterInst);
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
			VerifierParameterInst warningThresholdParameterInst)
			throws WrongDataTypeException {

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
			} else {
				throw new WrongDataTypeException(parameterInst);
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
			} else {
				throw new WrongDataTypeException(parameterInst);
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

	public String getDefaultErrorMessage() {
		return "Time validity rule not verified!";
	}

	public String getDefaultWarningMessage() {
		return "Default Warning!";
	}

	public String getDescription() {
		return this.DESCRIPTION;
	}

	public List<VerifierParameterDef> getInParams() {
		return this.params;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}

	public VerifierReport verify(Map<String, List<VerifierParameterInst>> params)
			throws WrongDataTypeException {
		List<VerifierParameterInst> earlierParametersInst = params
				.get(DateComparison.EARLIER_DATE);
		List<VerifierParameterInst> followingParametersInst = params
				.get(DateComparison.FOLLOWING_DATE);

		VerifierParameterInst warningThresholdParameterInst = params.get(
				DateComparison.WARNING_THRESHOLD).get(0);

		this.verifierReport = new VerifierReport();
		this.verifierReport.setResult(VerifierResult.PASSED);

		if (!earlierParametersInst.get(0).isFallible()) {
			this.fromEarliers(earlierParametersInst, followingParametersInst,
					warningThresholdParameterInst);
		} else if (!followingParametersInst.get(0).isFallible()) {
			this.fromFollowers(earlierParametersInst, followingParametersInst,
					warningThresholdParameterInst);
		} else if (earlierParametersInst.size() == 1) {
			this.fromEarliers(earlierParametersInst, followingParametersInst,
					warningThresholdParameterInst);
		} else {
			this.fromFollowers(earlierParametersInst, followingParametersInst,
					warningThresholdParameterInst);
		}

		return this.verifierReport;
	}

}
