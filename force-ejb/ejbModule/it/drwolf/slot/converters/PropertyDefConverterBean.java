package it.drwolf.slot.converters;

import it.drwolf.slot.entity.PropertyDef;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("propertyDefConverterBean")
@Scope(ScopeType.CONVERSATION)
public class PropertyDefConverterBean {

	private Map<String, PropertyDef> map = new HashMap<String, PropertyDef>();

	public void addPropertyDef(PropertyDef propertyDef) {
		if (this.map.get(propertyDef.getUuid()) == null) {
			this.map.put(propertyDef.getUuid(), propertyDef);
		}
	}

	public Map<String, PropertyDef> getMap() {
		return this.map;
	}

	public void setMap(Map<String, PropertyDef> map) {
		this.map = map;
	}

}
