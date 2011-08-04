package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataInstanceMultiplicity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

public class MultiplePropertyInst extends PropertyInst {

	// private Long id;

	// private PropertyDef propertyDef;

	// private SlotInst slotInst;

	private Set<String> values = new HashSet<String>();

	public MultiplePropertyInst() {
		this.setMultiplicity(DataInstanceMultiplicity.MULTIPLE);
	}

	public MultiplePropertyInst(PropertyDef propertyDef, SlotInst slotInst) {
		super(propertyDef, slotInst);
		this.setMultiplicity(DataInstanceMultiplicity.MULTIPLE);
	}

	public MultiplePropertyInst(PropertyDef propertyDef) {
		super(propertyDef);
		this.setMultiplicity(DataInstanceMultiplicity.MULTIPLE);
	}

	// @Id
	// @GeneratedValue
	// public Long getId() {
	// return id;
	// }

	// public void setId(Long id) {
	// this.id = id;
	// }
	//
	// @ManyToOne
	// public PropertyDef getPropertyDef() {
	// return propertyDef;
	// }
	//
	// public void setPropertyDef(PropertyDef propertyDef) {
	// this.propertyDef = propertyDef;
	// }
	//
	// @ManyToOne
	// public SlotInst getSlotInst() {
	// return slotInst;
	// }
	//
	// public void setSlotInst(SlotInst slotInst) {
	// this.slotInst = slotInst;
	// }

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

	// @Transient
	// public DataDefinition getDataDefinition() {
	// return this.getPropertyDef();
	// }

	@Transient
	public List<String> getValuesAsList() {
		return new ArrayList<String>(this.values);
	}

	@Transient
	public void setValuesAsList(List<String> valuesAsList) {
		this.values = new HashSet<String>(valuesAsList);
	}

	@Transient
	public void clean() {
		this.getValues().clear();
	}

}
