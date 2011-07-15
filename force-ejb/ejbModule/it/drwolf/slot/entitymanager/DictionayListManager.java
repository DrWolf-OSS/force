package it.drwolf.slot.entitymanager;

import it.drwolf.slot.enums.DataType;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("dictionayListManager")
public class DictionayListManager {

	@In(create = true)
	private EntityManager entityManager;

	public List<String> retrieveByType(DataType dataType) {
		List<String> resultList = entityManager
				.createQuery("from Dictionary d where d.dataType=:dataType")
				.setParameter("dataType", dataType).getResultList();
		if (resultList != null) {
			return resultList;
		}
		return null;
	}

}
