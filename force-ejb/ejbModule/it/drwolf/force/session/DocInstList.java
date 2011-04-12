package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("docInstList")
public class DocInstList extends EntityQuery<DocInst> {

	private static final String EJBQL = "select docInst from DocInst docInst";

	private static final String[] RESTRICTIONS = { "lower(docInst.nodeRef) like lower(concat(#{docInstList.docInst.nodeRef},'%'))", };

	private DocInst docInst = new DocInst();

	public DocInstList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DocInst getDocInst() {
		return docInst;
	}
}
