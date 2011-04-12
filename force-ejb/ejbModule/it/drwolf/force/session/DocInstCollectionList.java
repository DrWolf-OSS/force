package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("docInstCollectionList")
public class DocInstCollectionList extends EntityQuery<DocInstCollection> {

	private static final String EJBQL = "select docInstCollection from DocInstCollection docInstCollection";

	private static final String[] RESTRICTIONS = {};

	private DocInstCollection docInstCollection = new DocInstCollection();

	public DocInstCollectionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DocInstCollection getDocInstCollection() {
		return docInstCollection;
	}
}
