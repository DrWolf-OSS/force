package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("propertyInstList")
public class PropertyInstList extends EntityQuery<PropertyInst> {

	private static final String EJBQL = "select propertyInst from PropertyInst propertyInst";

	private static final String[] RESTRICTIONS = { "lower(propertyInst.stringValue) like lower(concat(#{propertyInstList.propertyInst.stringValue},'%'))", };

	private PropertyInst propertyInst = new PropertyInst();

	public PropertyInstList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PropertyInst getPropertyInst() {
		return propertyInst;
	}
}
