package it.drwolf.slot.session;

import it.drwolf.slot.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("docDefCollectionList")
public class DocDefCollectionList extends EntityQuery<DocDefCollection> {

	private static final String EJBQL = "select docDefCollection from DocDefCollection docDefCollection";

	private static final String[] RESTRICTIONS = { "lower(docDefCollection.name) like lower(concat(#{docDefCollectionList.docDefCollection.name},'%'))", };

	private DocDefCollection docDefCollection = new DocDefCollection();

	public DocDefCollectionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DocDefCollection getDocDefCollection() {
		return docDefCollection;
	}
}
