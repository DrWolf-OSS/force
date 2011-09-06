package it.drwolf.slot.pagebeans;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.listeners.RuleListener;
import it.drwolf.slot.enums.RuleType;
import it.drwolf.slot.interfaces.IRuleVerifier;
import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;
import it.drwolf.slot.pagebeans.support.PropertyContainer;
import it.drwolf.slot.ruleverifier.RuleParametersResolver;
import it.drwolf.slot.ruleverifier.VerifierParameterDef;
import it.drwolf.slot.session.RuleHome;
import it.drwolf.slot.session.SlotDefHome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("ruleEditBean")
@Scope(ScopeType.CONVERSATION)
public class RuleEditBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	@In(create = true)
	private RuleHome ruleHome;

	@In(create = true)
	private RuleParametersResolver ruleParametersResolver;

	private RuleListener ruleListener = new RuleListener();

	private HashMap<String, List<PropertiesSourceContainer>> sourcePropertiesSourceMap = new HashMap<String, List<PropertiesSourceContainer>>();

	private HashMap<String, PropertiesSourceContainer> targetPropertiesSourceMap = new HashMap<String, PropertiesSourceContainer>();
	private HashMap<String, PropertyContainer> targetPropertyMap = new HashMap<String, PropertyContainer>();

	private List<VerifierParameterDef> normalParameters = new ArrayList<VerifierParameterDef>();

	private List<RuleParameterInst> embeddedParameters = new ArrayList<RuleParameterInst>();

	private RuleParameterInst activeEmbeddedParameter;

	//
	private List<PropertiesSourceContainer> propertiesSources = new ArrayList<PropertiesSourceContainer>();

	@Factory("ruleTypes")
	public List<RuleType> getRuleTypes() {
		return Arrays.asList(RuleType.values());
	}

	@Create
	public void init() {
		Rule rule = ruleHome.getInstance();
		IRuleVerifier verifier = rule.getVerifier();
		if (verifier != null) {

			List<VerifierParameterDef> inParams = verifier.getInParams();
			Map<String, String> encodedParametersMap = rule.getParametersMap();
			for (VerifierParameterDef parameterDef : inParams) {
				String paramName = parameterDef.getName();

				RuleParameterInst ruleParameterInst = rule
						.getEmbeddedParametersMap().get(paramName);
				if (ruleParameterInst != null) {
					embeddedParameters.add(ruleParameterInst);
				} else {
					//
					this.normalParameters.add(parameterDef);
					//
					String encodedParams = encodedParametersMap.get(paramName);
					if (encodedParams != null && !encodedParams.equals("")) {
						String[] splitted = encodedParams.split("\\|");
						String source = splitted[0];
						String field = splitted[1];
						Object sourceDef = ruleParametersResolver
								.resolveSourceDef(source);
						Object fieldDef = ruleParametersResolver
								.resolveFieldDef(field);

						if (!(sourceDef instanceof Rule)) {
							PropertiesSourceContainer propertiesSourceContainer = new PropertiesSourceContainer(
									sourceDef);
							targetPropertiesSourceMap.put(paramName,
									propertiesSourceContainer);

							PropertyContainer propertyContainer = new PropertyContainer(
									fieldDef);
							targetPropertyMap.put(paramName, propertyContainer);
						}

					}
				}
			}

			//
			buildSources();
		}
	}

	public void ruleTypeListener(ActionEvent event) {
		Rule instance = this.ruleHome.getInstance();
		ruleListener.setVerifier(instance);
		IRuleVerifier verifier = instance.getVerifier();
		sourcePropertiesSourceMap = new HashMap<String, List<PropertiesSourceContainer>>();

		if (verifier != null) {
			List<VerifierParameterDef> inParams = verifier.getInParams();
			//
			this.embeddedParameters.clear();
			this.normalParameters.clear();
			for (VerifierParameterDef verifierParameter : inParams) {
				if (verifierParameter.isRuleEmbedded()) {
					// this.embeddedParameters.add(verifierParameter);
					RuleParameterInst embeddedParameterInst = new RuleParameterInst();
					embeddedParameterInst
							.setVerifierParameterDef(verifierParameter);
					// embeddedParameterInst.setParameterName(verifierParameter
					// .getName());
					embeddedParameterInst.setRule(instance);
					embeddedParameters.add(embeddedParameterInst);
				} else {
					this.normalParameters.add(verifierParameter);
				}
			}
			//
			buildSources();

		} else {
			this.normalParameters.clear();
			this.embeddedParameters.clear();

			targetPropertiesSourceMap.clear();
			targetPropertyMap.clear();
		}
	}

	private void buildSources() {
		SlotDef slotDef = slotDefHome.getInstance();
		Set<DocDefCollection> docDefCollections = slotDef
				.getDocDefCollections();

		// for (VerifierParameterDef verifierParameter : normalParameters) {
		ArrayList<PropertiesSourceContainer> propertiesSourceContainerList = new ArrayList<PropertiesSourceContainer>();
		for (DocDefCollection collection : docDefCollections) {
			PropertiesSourceContainer sourceContainer = new PropertiesSourceContainer(
					collection);
			propertiesSourceContainerList.add(sourceContainer);
		}
		propertiesSourceContainerList
				.add(new PropertiesSourceContainer(slotDef));
		// sourcePropertiesSourceMap.put(verifierParameter.getName(),
		// propertiesSourceContainerList);
		// }

		//
		this.propertiesSources = propertiesSourceContainerList;
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
				} else {
					if (!parameter.isOptional() && !isInEmbedded(parameter)) {
						error = true;
						FacesMessages.instance()
								.add(Severity.ERROR,
										"\"" + parameter.getLabel()
												+ "\" not compiled", null);
					}
				}
			}

			for (RuleParameterInst embeddedParameter : embeddedParameters) {
				embeddedParameter.setRule(rule);
				rule.getEmbeddedParametersMap().put(
						embeddedParameter.getVerifierParameterDef().getName(),
						embeddedParameter);
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

	public void update() {
		this.save();
	}

	private boolean isInEmbedded(VerifierParameterDef parameterDef) {
		Iterator<RuleParameterInst> iterator = this.embeddedParameters
				.iterator();
		while (iterator.hasNext()) {
			RuleParameterInst parameterInst = iterator.next();
			if (parameterInst.getVerifierParameterDef().equals(parameterDef)) {
				return true;
			}
		}
		return false;
	}

	public void setTmpParameter(VerifierParameterDef parameterDef) {
		RuleParameterInst parameterInst = new RuleParameterInst();
		parameterInst.setVerifierParameterDef(parameterDef);
		// parameterInst.setParameterName(parameterDef.getName());
		this.activeEmbeddedParameter = parameterInst;
	}

	public void addEmbeddedParameter() {
		//
		// vanno messi in una collection a parte per poi poterli riconoscere e
		// togliere
		this.embeddedParameters.add(this.activeEmbeddedParameter);
		this.normalParameters.remove(this.activeEmbeddedParameter
				.getVerifierParameterDef());
	}

	public void removeEmbeddedParameter(RuleParameterInst parameterInst) {
		this.embeddedParameters.remove(parameterInst);
		this.normalParameters.add(parameterInst.getVerifierParameterDef());
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

	public List<VerifierParameterDef> getNormalParameters() {
		return normalParameters;
	}

	public void setNormalParameters(List<VerifierParameterDef> normalParameters) {
		this.normalParameters = normalParameters;
	}

	public List<RuleParameterInst> getEmbeddedParameters() {
		return embeddedParameters;
	}

	public void setEmbeddedParameters(List<RuleParameterInst> embeddedParameters) {
		this.embeddedParameters = embeddedParameters;
	}

	public RuleParameterInst getActiveEmbeddedParameter() {
		return activeEmbeddedParameter;
	}

	public void setActiveEmbeddedParameter(
			RuleParameterInst activeEmbeddedParameter) {
		this.activeEmbeddedParameter = activeEmbeddedParameter;
	}

	public List<PropertiesSourceContainer> getPropertiesSources() {
		return propertiesSources;
	}

	public void setPropertiesSources(
			List<PropertiesSourceContainer> propertiesSources) {
		this.propertiesSources = propertiesSources;
	}

}
