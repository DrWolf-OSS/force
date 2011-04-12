package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("propertytDefList")
public class PropertytDefList extends EntityQuery<PropertytDef> {

	private static final String EJBQL = "select propertytDef from PropertytDef propertytDef";

	private static final String[] RESTRICTIONS = { "lower(propertytDef.name) like lower(concat(#{propertytDefList.propertytDef.name},'%'))", };

	private PropertytDef propertytDef = new PropertytDef();

	public PropertytDefList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PropertytDef getPropertytDef() {
		return propertytDef;
	}
}
