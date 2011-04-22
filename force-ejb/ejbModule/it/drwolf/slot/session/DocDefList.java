package it.drwolf.slot.session;

import it.drwolf.slot.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("docDefList")
public class DocDefList extends EntityQuery<DocDef> {

	private static final String EJBQL = "select docDef from DocDef docDef";

	private static final String[] RESTRICTIONS = { "lower(docDef.name) like lower(concat(#{docDefList.docDef.name},'%'))", };

	private DocDef docDef = new DocDef();

	public DocDefList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DocDef getDocDef() {
		return docDef;
	}
}
