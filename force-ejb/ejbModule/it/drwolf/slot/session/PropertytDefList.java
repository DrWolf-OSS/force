package it.drwolf.slot.session;

import it.drwolf.slot.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("propertytDefList")
public class PropertytDefList extends EntityQuery<PropertyDef> {

	private static final String EJBQL = "select propertytDef from PropertytDef propertytDef";

	private static final String[] RESTRICTIONS = { "lower(propertytDef.name) like lower(concat(#{propertytDefList.propertytDef.name},'%'))", };

	private PropertyDef propertytDef = new PropertyDef();

	public PropertytDefList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PropertyDef getPropertytDef() {
		return propertytDef;
	}
}
