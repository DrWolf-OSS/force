package it.drwolf.slot.session;

import it.drwolf.slot.entity.Dictionary;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("dictionaryList")
public class DictionaryList extends EntityQuery<Dictionary> {

	private static final String EJBQL = "select dictionary from Dictionary dictionary";

	private static final String[] RESTRICTIONS = { "lower(dictionary.name) like lower(concat(#{dictionaryList.dictionary.name},'%'))", };

	private Dictionary dictionary = new Dictionary();

	public DictionaryList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Dictionary getDictionary() {
		return dictionary;
	}
}
