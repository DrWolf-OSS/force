package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("ruleList")
public class RuleList extends EntityQuery<Rule> {

	private static final String EJBQL = "select rule from Rule rule";

	private static final String[] RESTRICTIONS = {};

	private Rule rule = new Rule();

	public RuleList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Rule getRule() {
		return rule;
	}
}
