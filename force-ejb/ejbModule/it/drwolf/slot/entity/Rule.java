package it.drwolf.slot.entity;

import it.drwolf.slot.entity.listeners.RuleListener;
import it.drwolf.slot.enums.RuleType;
import it.drwolf.slot.interfaces.IRuleVerifier;
import it.drwolf.slot.ruleverifier.VerifierMessage;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

@Entity
@EntityListeners(value = RuleListener.class)
public class Rule {

	private Long id;

	private SlotDef slotDef;

	private Map<String, String> parametersMap = new HashMap<String, String>();

	private RuleType type;

	private IRuleVerifier verifier;

	private VerifierMessage errorMessage;

	private VerifierMessage warningMessage;

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

	@OneToOne(cascade = CascadeType.ALL)
	public VerifierMessage getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(VerifierMessage errorMessage) {
		this.errorMessage = errorMessage;
	}

	@OneToOne(cascade = CascadeType.ALL)
	public VerifierMessage getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(VerifierMessage warningMessage) {
		this.warningMessage = warningMessage;
	}

}
