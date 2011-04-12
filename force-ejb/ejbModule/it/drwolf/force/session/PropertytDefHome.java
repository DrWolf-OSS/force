package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("propertytDefHome")
public class PropertytDefHome extends EntityHome<PropertytDef> {

	public void setPropertytDefId(Long id) {
		setId(id);
	}

	public Long getPropertytDefId() {
		return (Long) getId();
	}

	@Override
	protected PropertytDef createInstance() {
		PropertytDef propertytDef = new PropertytDef();
		return propertytDef;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public PropertytDef getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
