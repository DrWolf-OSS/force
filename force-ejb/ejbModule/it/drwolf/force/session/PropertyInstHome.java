package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("propertyInstHome")
public class PropertyInstHome extends EntityHome<PropertyInst> {

	@In(create = true)
	PropertytDefHome propertytDefHome;

	public void setPropertyInstId(Long id) {
		setId(id);
	}

	public Long getPropertyInstId() {
		return (Long) getId();
	}

	@Override
	protected PropertyInst createInstance() {
		PropertyInst propertyInst = new PropertyInst();
		return propertyInst;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PropertyDef propertytDef = propertytDefHome.getDefinedInstance();
		if (propertytDef != null) {
			getInstance().setPropertytDef(propertytDef);
		}
	}

	public boolean isWired() {
		return true;
	}

	public PropertyInst getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
