package it.drwolf.slot.entitymanager;

import it.drwolf.slot.enums.DataType;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("constraintListManager")
public class ConstraintListManager {

	@In(create = true)
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<String> retrieveByType(DataType dataType) {
		List<String> resultList = this.entityManager
				.createQuery("from Constraint c where c.dataType=:dataType")
				.setParameter("dataType", dataType).getResultList();
		if (resultList != null) {
			return resultList;
		}
		return null;
	}
}
