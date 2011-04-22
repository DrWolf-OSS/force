package it.drwolf.slot.entity;

import it.drwolf.slot.entity.listeners.RuleListener;
import it.drwolf.slot.enums.RuleType;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

}
