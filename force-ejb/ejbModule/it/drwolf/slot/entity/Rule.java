package it.drwolf.slot.entity;

import it.drwolf.slot.entity.listeners.RuleListener;
import it.drwolf.slot.enums.RuleType;
import it.drwolf.slot.interfaces.Deactivable;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;

@Entity
@EntityListeners(value = RuleListener.class)
public class Rule implements Deactivable {

	private Long id;

	private SlotDef slotDef;

	private Map<String, String> parametersMap = new HashMap<String, String>();

	private Map<String, RuleParameterInst> embeddedParametersMap = new HashMap<String, RuleParameterInst>();

	private RuleType type;

	private boolean mandatory = false;

	private IRuleVerifier verifier;

	private String errorMessage;

	private String warningMessage;

	private boolean active = true;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@CollectionOfElements
	// @MapKey(name = "mapKey")
	// @Column(name = "mapValue")
	public Map<String, String> getParametersMap() {
		return parametersMap;
	}

	public void setParametersMap(Map<String, String> parametersMap) {
		this.parametersMap = parametersMap;
	}

	@Enumerated(EnumType.STRING)
	public RuleType getType() {
		return type;
	}

	public void setType(RuleType type) {
		this.type = type;
	}

	@Transient
	public IRuleVerifier getVerifier() {
		return verifier;
	}

	public void setVerifier(IRuleVerifier verifier) {
		this.verifier = verifier;
	}

	@ManyToOne
	public SlotDef getSlotDef() {
		return slotDef;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@MapKey(name = "parameterName")
	@OneToMany(mappedBy = "rule", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public Map<String, RuleParameterInst> getEmbeddedParametersMap() {
		return embeddedParametersMap;
	}

	public void setEmbeddedParametersMap(
			Map<String, RuleParameterInst> embeddedParametersMap) {
		this.embeddedParametersMap = embeddedParametersMap;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	@Transient
	public List<RuleParameterInst> getEmbeddedParametersAsList() {
		return new ArrayList<RuleParameterInst>(this.getEmbeddedParametersMap()
				.values());
	}

	@Transient
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;

	}

}
