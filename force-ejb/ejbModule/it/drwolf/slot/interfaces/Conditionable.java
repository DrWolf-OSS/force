package it.drwolf.slot.interfaces;

import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;

public interface Conditionable extends Deactivable {

	public Long getId();

	public PropertyDef getConditionalPropertyDef();

	public void setConditionalPropertyDef(PropertyDef conditionalPropertyDef);

	public PropertyInst getConditionalPropertyInst();

	public void setConditionalPropertyInst(PropertyInst conditionalPropertyInst);

}
