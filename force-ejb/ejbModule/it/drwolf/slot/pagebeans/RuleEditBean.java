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
import it.drwolf.slot.ruleverifier.RuleParametersEncoder;
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

	@In(create = true)
	private RuleParametersEncoder ruleParametersEncoder;

	private RuleListener ruleListener = new RuleListener();

	private HashMap<String, List<PropertiesSourceContainer>> sourcePropertiesSourceMap = new HashMap<String, List<PropertiesSourceContainer>>();

	private HashMap<String, PropertiesSourceContainer> targetPropertiesSourceMap = new HashMap<String, PropertiesSourceContainer>();
	private HashMap<String, PropertyContainer> targetPropertyMap = new HashMap<String, PropertyContainer>();

	private List<VerifierParameterDef> normalParameters = new ArrayList<VerifierParameterDef>();

	private RuleParameterInst activeEmbeddedParameter;

	//
	private List<PropertiesSourceContainer> propertiesSources = new ArrayList<PropertiesSourceContainer>();

	public void addEmbeddedParameter() {
		//
		// vanno messi in una collection a parte per poi poterli riconoscere e
		// togliere
		// this.embeddedParameters.add(this.activeEmbeddedParameter);
		this.ruleHome
				.getInstance()
				.getEmbeddedParametersMap()
				.put(this.activeEmbeddedParameter.getVerifierParameterDef()
						.getName(), this.activeEmbeddedParameter);
		this.activeEmbeddedParameter.setRule(this.ruleHome.getInstance());
		//
		this.normalParameters.remove(this.activeEmbeddedParameter
				.getVerifierParameterDef());
	}

	private void buildSources() {
		SlotDef slotDef = this.slotDefHome.getInstance();
		Set<DocDefCollection> docDefCollections = slotDef
				.getDocDefCollections();

		ArrayList<PropertiesSourceContainer> propertiesSourceContainerList = new ArrayList<PropertiesSourceContainer>();
		for (DocDefCollection collection : docDefCollections) {
			PropertiesSourceContainer sourceContainer = new PropertiesSourceContainer(
					collection);
			propertiesSourceContainerList.add(sourceContainer);
		}
		propertiesSourceContainerList
				.add(new PropertiesSourceContainer(slotDef));
		this.propertiesSources = propertiesSourceContainerList;
	}

	public RuleParameterInst getActiveEmbeddedParameter() {
		return this.activeEmbeddedParameter;
	}

	public List<VerifierParameterDef> getNormalParameters() {
		return this.normalParameters;
	}

	public List<PropertiesSourceContainer> getPropertiesSources() {
		return this.propertiesSources;
	}

	@Factory("ruleTypes")
	public List<RuleType> getRuleTypes() {
		return Arrays.asList(RuleType.values());
	}

	public HashMap<String, List<PropertiesSourceContainer>> getSourcePropertiesSourceMap() {
		return this.sourcePropertiesSourceMap;
	}

	public HashMap<String, PropertiesSourceContainer> getTargetPropertiesSourceMap() {
		return this.targetPropertiesSourceMap;
	}

	public HashMap<String, PropertyContainer> getTargetPropertyMap() {
		return this.targetPropertyMap;
	}

	@Create
	public void init() {
		Rule rule = this.ruleHome.getInstance();
		IRuleVerifier verifier = rule.getVerifier();
		if (verifier != null) {
			List<VerifierParameterDef> inParams = verifier.getInParams();
			Map<String, String> encodedParametersMap = rule.getParametersMap();
			for (VerifierParameterDef parameterDef : inParams) {
				String paramName = parameterDef.getName();

				RuleParameterInst ruleParameterInst = rule
						.getEmbeddedParametersMap().get(paramName);
				if (ruleParameterInst == null) {
					this.normalParameters.add(parameterDef);
					String encodedParams = encodedParametersMap.get(paramName);
					if (encodedParams != null && !encodedParams.equals("")) {
						String[] splitted = encodedParams.split("\\|");
						String source = splitted[0];
						String field = splitted[1];
						Object sourceDef = this.ruleParametersResolver
								.resolveSourceDef(source);
						Object fieldDef = this.ruleParametersResolver
								.resolveFieldDef(field);

						if (!(sourceDef instanceof Rule)) {
							PropertiesSourceContainer propertiesSourceContainer = new PropertiesSourceContainer(
									sourceDef);
							this.targetPropertiesSourceMap.put(paramName,
									propertiesSourceContainer);

							PropertyContainer propertyContainer = new PropertyContainer(
									fieldDef);
							this.targetPropertyMap.put(paramName,
									propertyContainer);
						}

					}
				}
			}

			this.buildSources();
		}
	}

	private boolean isInEmbedded(VerifierParameterDef parameterDef) {
		Iterator<RuleParameterInst> iterator = this.ruleHome.getInstance()
				.getEmbeddedParametersMap().values().iterator();
		//
		while (iterator.hasNext()) {
			RuleParameterInst parameterInst = iterator.next();
			if (parameterInst.getVerifierParameterDef().equals(parameterDef)) {
				return true;
			}
		}
		return false;
	}

	public void removeEmbeddedParameter(RuleParameterInst parameterInst) {
		this.ruleHome.getInstance().getEmbeddedParametersMap()
				.remove(parameterInst.getVerifierParameterDef().getName());
		parameterInst.setRule(null);
		this.normalParameters.add(parameterInst.getVerifierParameterDef());
	}

	public void ruleTypeListener(ActionEvent event) {
		Rule instance = this.ruleHome.getInstance();
		this.ruleListener.setVerifier(instance);
		IRuleVerifier verifier = instance.getVerifier();
		this.sourcePropertiesSourceMap = new HashMap<String, List<PropertiesSourceContainer>>();

		if (verifier != null) {
			List<VerifierParameterDef> inParams = verifier.getInParams();
			this.normalParameters.clear();
			for (VerifierParameterDef verifierParameter : inParams) {
				if (verifierParameter.isRuleEmbedded()) {
					RuleParameterInst embeddedParameterInst = new RuleParameterInst();
					embeddedParameterInst
							.setVerifierParameterDef(verifierParameter);
					embeddedParameterInst.setRule(instance);

					this.ruleHome
							.getInstance()
							.getEmbeddedParametersMap()
							.put(verifierParameter.getName(),
									embeddedParameterInst);
				} else {
					this.normalParameters.add(verifierParameter);
				}
			}
			//
			this.buildSources();

		} else {
			this.normalParameters.clear();
			this.targetPropertiesSourceMap.clear();
			this.targetPropertyMap.clear();
		}
	}

	public String save() {
		boolean error = false;
		Rule rule = this.ruleHome.getInstance();
		IRuleVerifier verifier = rule.getVerifier();
		Map<String, String> parametersMap = rule.getParametersMap();
		if (verifier != null) {
			for (VerifierParameterDef parameter : verifier.getInParams()) {
				if (this.targetPropertiesSourceMap.get(parameter.getName()) != null
						&& this.targetPropertyMap.get(parameter.getName()) != null) {
					PropertiesSourceContainer propertiesSourceContainer = this.targetPropertiesSourceMap
							.get(parameter.getName());
					PropertyContainer propertyContainer = this.targetPropertyMap
							.get(parameter.getName());
					// String encodedRule = "";
					// encodedRule =
					// encodedRule.concat(propertiesSourceContainer
					// .toString());
					// encodedRule = encodedRule.concat("|"
					// + propertyContainer.toString());
					String encodedRule = this.ruleParametersEncoder.encode(
							propertiesSourceContainer, propertyContainer);
					parametersMap.put(parameter.getName(), encodedRule);
				} else {
					if (!parameter.isOptional()
							&& !this.isInEmbedded(parameter)) {
						error = true;
						FacesMessages.instance()
								.add(Severity.ERROR,
										"\"" + parameter.getLabel()
												+ "\" not compiled", null);
					}
				}
			}

		} else {
			error = true;
			FacesMessages.instance().add(
					"Select a rule type and compile the rule");
			return "failed";
		}

		if (!error) {
			return this.ruleHome.persist();
		}

		return "failed";
	}

	public void setActiveEmbeddedParameter(
			RuleParameterInst activeEmbeddedParameter) {
		this.activeEmbeddedParameter = activeEmbeddedParameter;
	}

	public void setNormalParameters(List<VerifierParameterDef> normalParameters) {
		this.normalParameters = normalParameters;
	}

	public void setPropertiesSources(
			List<PropertiesSourceContainer> propertiesSources) {
		this.propertiesSources = propertiesSources;
	}

	public void setSourcePropertiesSourceMap(
			HashMap<String, List<PropertiesSourceContainer>> sourcePropertiesSourceMap) {
		this.sourcePropertiesSourceMap = sourcePropertiesSourceMap;
	}

	public void setTargetPropertiesSourceMap(
			HashMap<String, PropertiesSourceContainer> targetPropertiesSourceMap) {
		this.targetPropertiesSourceMap = targetPropertiesSourceMap;
	}

	public void setTargetPropertyMap(
			HashMap<String, PropertyContainer> targetPropertyMap) {
		this.targetPropertyMap = targetPropertyMap;
	}

	public void setTmpParameter(VerifierParameterDef parameterDef) {
		RuleParameterInst parameterInst = new RuleParameterInst();
		parameterInst.setVerifierParameterDef(parameterDef);
		this.activeEmbeddedParameter = parameterInst;
	}

	public String update() {
		return this.save();
	}

}
