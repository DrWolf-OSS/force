package it.drwolf.slot.entity;

import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

@Entity
public class MultiplePropertyInst implements DataInstance {

	private Long id;

	private PropertyDef propertyDef;

	private SlotInst slotInst;

	private Set<String> values = new HashSet<String>();

	public MultiplePropertyInst() {
	}

	public MultiplePropertyInst(PropertyDef propertyDef, SlotInst slotInst) {
		super();
		this.propertyDef = propertyDef;
		this.slotInst = slotInst;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public PropertyDef getPropertyDef() {
		return propertyDef;
	}

	public void setPropertyDef(PropertyDef propertyDef) {
		this.propertyDef = propertyDef;
	}

	@ManyToOne
	public SlotInst getSlotInst() {
		return slotInst;
	}

	public void setSlotInst(SlotInst slotInst) {
		this.slotInst = slotInst;
	}

	@CollectionOfElements
	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}

	@Transient
	public Object getValue() {
		return this.getValues();
	}

	@Transient
	public DataDefinition getDataDefinition() {
		return this.getPropertyDef();
	}

	@Transient
	public List<String> getValuesAsList() {
		return new ArrayList<String>(this.values);
	}

	@Transient
	public void setValuesAsList(List<String> valuesAsList) {
		this.values = new HashSet<String>(valuesAsList);
	}

}
