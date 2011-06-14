package it.drwolf.slot.pagebeans;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.listeners.RuleListener;
import it.drwolf.slot.enums.RuleType;
import it.drwolf.slot.interfaces.IRuleVerifier;
import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;
import it.drwolf.slot.pagebeans.support.PropertyContainer;
import it.drwolf.slot.ruleverifier.VerifierMessage;
import it.drwolf.slot.ruleverifier.VerifierMessageType;
import it.drwolf.slot.ruleverifier.VerifierParameterDef;
import it.drwolf.slot.session.RuleHome;
import it.drwolf.slot.session.SlotDefHome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Name("ruleEditBean")
@Scope(ScopeType.CONVERSATION)
public class RuleEditBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	@In(create = true)
	private RuleHome ruleHome;

	private RuleListener ruleListener = new RuleListener();

	private HashMap<String, List<PropertiesSourceContainer>> sourcePropertiesSourceMap = new HashMap<String, List<PropertiesSourceContainer>>();

	private HashMap<String, PropertiesSourceContainer> targetPropertiesSourceMap = new HashMap<String, PropertiesSourceContainer>();
	private HashMap<String, PropertyContainer> targetPropertyMap = new HashMap<String, PropertyContainer>();

	private VerifierMessage errorMessage = new VerifierMessage("",
			VerifierMessageType.ERROR);
	private VerifierMessage warningMessage = new VerifierMessage("",
			VerifierMessageType.WARNING);

	@Factory("ruleTypes")
	public List<RuleType> getRuleTypes() {
		return Arrays.asList(RuleType.values());
	}

	public void ruleTypeListener(ActionEvent event) {
		Rule instance = this.ruleHome.getInstance();
		ruleListener.setVerifier(instance);
		IRuleVerifier verifier = instance.getVerifier();
		sourcePropertiesSourceMap = new HashMap<String, List<PropertiesSourceContainer>>();

		if (verifier != null) {
			List<VerifierParameterDef> inParams = verifier.getInParams();
			SlotDef slotDef = slotDefHome.getInstance();
			Set<DocDefCollection> docDefCollections = slotDef
					.getDocDefCollections();

			for (VerifierParameterDef verifierParameter : inParams) {
				ArrayList<PropertiesSourceContainer> propertiesSourceContainerList = new ArrayList<PropertiesSourceContainer>();
				for (DocDefCollection collection : docDefCollections) {
					PropertiesSourceContainer sourceContainer = new PropertiesSourceContainer(
							collection);
					propertiesSourceContainerList.add(sourceContainer);
				}
				propertiesSourceContainerList
						.add(new PropertiesSourceContainer(slotDef));
				sourcePropertiesSourceMap.put(verifierParameter.getName(),
						propertiesSourceContainerList);
			}
			// System.out.println("---> assigned listener " + verifier);
		} else {
			targetPropertiesSourceMap.clear();
			targetPropertyMap.clear();
		}
	}

	public void save() {
		boolean error = false;
		Rule rule = ruleHome.getInstance();
		IRuleVerifier verifier = rule.getVerifier();
		Map<String, String> parametersMap = rule.getParametersMap();
		if (verifier != null) {
			for (VerifierParameterDef parameter : verifier.getInParams()) {
				if (this.targetPropertiesSourceMap.get(parameter.getName()) != null
						&& this.targetPropertyMap.get(parameter.getName()) != null) {
					String encodedRule = "";
					PropertiesSourceContainer propertiesSourceContainer = this.targetPropertiesSourceMap
							.get(parameter.getName());
					encodedRule = encodedRule.concat(propertiesSourceContainer
							.toString());
					PropertyContainer propertyContainer = this.targetPropertyMap
							.get(parameter.getName());
					encodedRule = encodedRule.concat("|"
							+ propertyContainer.toString());
					parametersMap.put(parameter.getName(), encodedRule);
					if (!this.errorMessage.getText().equals("")) {
						rule.setErrorMessage(this.errorMessage);
					}
					if (!this.warningMessage.getText().equals("")) {
						rule.setWarningMessage(this.warningMessage);
					}
				} else {
					if (!parameter.isOptional()) {
						error = true;
						FacesMessages.instance()
								.add("\"" + parameter.getLabel()
										+ "\" not compiled");
					}
				}
			}
		} else {
			error = true;
			FacesMessages.instance().add(
					"Select a rule type and compile the rule");
		}

		if (!error) {
			ruleHome.persist();
		}
	}

	public HashMap<String, List<PropertiesSourceContainer>> getSourcePropertiesSourceMap() {
		return sourcePropertiesSourceMap;
	}

	public void setSourcePropertiesSourceMap(
			HashMap<String, List<PropertiesSourceContainer>> sourcePropertiesSourceMap) {
		this.sourcePropertiesSourceMap = sourcePropertiesSourceMap;
	}

	public HashMap<String, PropertiesSourceContainer> getTargetPropertiesSourceMap() {
		return targetPropertiesSourceMap;
	}

	public void setTargetPropertiesSourceMap(
			HashMap<String, PropertiesSourceContainer> targetPropertiesSourceMap) {
		this.targetPropertiesSourceMap = targetPropertiesSourceMap;
	}

	public HashMap<String, PropertyContainer> getTargetPropertyMap() {
		return targetPropertyMap;
	}

	public void setTargetPropertyMap(
			HashMap<String, PropertyContainer> targetPropertyMap) {
		this.targetPropertyMap = targetPropertyMap;
	}

	public VerifierMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(VerifierMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

	public VerifierMessage getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(VerifierMessage warningMessage) {
		this.warningMessage = warningMessage;
	}

}
