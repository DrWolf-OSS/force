package it.drwolf.slot.entity;

import it.drwolf.slot.enums.SlotDefType;
import it.drwolf.slot.interfaces.Conditionable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.annotations.Cascade;

@Entity
public class DependentSlotDef extends SlotDef implements Conditionable {

	private PropertyDef conditionalPropertyDef;

	private PropertyInst conditionalPropertyInst;

	private boolean active = Boolean.TRUE;

	private SlotDef parentSlotDef;

	public DependentSlotDef() {
		this.setType(SlotDefType.DEPENDENT);
	}

	public DependentSlotDef(SlotDef slotDef) {
		try {
			this.setType(SlotDefType.DEPENDENT);
			Map properties = BeanUtils.describe(slotDef);
			// DependentSlotDef dependentSlotDef = new DependentSlotDef();
			// dependentSlotDef.se
			//
			// BeanUtils.populate(dependentSlotDef, properties);

			System.out.println("---> break");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// FARE CLONATORE DA SLOT_DEF
	}

	@ManyToOne(cascade = CascadeType.PERSIST)
	public PropertyDef getConditionalPropertyDef() {
		return this.conditionalPropertyDef;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public PropertyInst getConditionalPropertyInst() {
		return this.conditionalPropertyInst;
	}

	@ManyToOne
	public SlotDef getParentSlotDef() {
		return this.parentSlotDef;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setConditionalPropertyDef(PropertyDef conditionalPropertyDef) {
		this.conditionalPropertyDef = conditionalPropertyDef;
	}

	public void setConditionalPropertyInst(PropertyInst conditionalPropertyInst) {
		this.conditionalPropertyInst = conditionalPropertyInst;
	}

	public void setParentSlotDef(SlotDef parentSlotDef) {
		this.parentSlotDef = parentSlotDef;
	}

}
