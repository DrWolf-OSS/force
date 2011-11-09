package it.drwolf.slot.entitymanager;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.enums.SlotDefType;
import it.drwolf.slot.pagebeans.SlotDefParameters;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

@Scope(ScopeType.CONVERSATION)
@Name("slotDefListManager")
public class SlotDefListManager {

	@In(create = true)
	private EntityManager entityManager;

	@In(create = true)
	private SlotDefParameters slotDefParameters;

	@In
	private AlfrescoUserIdentity alfrescoUserIdentity;

	@In
	private Identity identity;

	@SuppressWarnings("unchecked")
	private List<SlotDef> retrieve(Boolean areTemplates,
			SlotDefType slotDefType, String ownerId) {
		int count = 0;
		String templates = "";
		if (areTemplates != null) {
			templates = "s.template=:areTemplates";
			count++;
		}

		String type = "";
		if (slotDefType != null) {
			type = "s.type=:slotDefType";
			count++;
		}

		String owner = "";
		if (ownerId != null && !ownerId.equals("")) {
			owner = "s.ownerId=:ownerId";
			count++;
		}

		String where = "";
		String and = "";
		String and2 = "";
		if (count > 0) {
			where = "where";
		}
		if (count > 1) {
			and = "and";
		}
		if (count > 2) {
			and2 = "and";
		}

		String query = "from SlotDef s " + where + " " + templates + " " + and
				+ " " + type + " " + and2 + " " + owner;

		// List<SlotDef> resultList = null;
		Query createQuery = this.entityManager.createQuery(query);
		if (areTemplates != null) {
			createQuery.setParameter("areTemplates", areTemplates);
		}
		if (slotDefType != null) {
			createQuery.setParameter("slotDefType", slotDefType);
		}
		if (ownerId != null && !ownerId.equals("")) {
			createQuery.setParameter("ownerId", ownerId);
		}

		return createQuery.getResultList();
	}

	@Factory("allGeneralTemplates")
	public List<SlotDef> retrieveAllGeneralTemplates() {
		return this.retrieve(true, SlotDefType.GENERAL, null);
	}

	@Factory("allPrimaryTemplates")
	public List<SlotDef> retrieveAllPrimaryTemplates() {
		return this.retrieve(true, SlotDefType.PRIMARY, null);
	}

	@Factory("slotDefsByParams")
	public List<SlotDef> retrieveByParams() {
		String ownerId = null;
		if (!this.identity.hasRole("ADMIN")) {
			ownerId = this.alfrescoUserIdentity.getActiveGroup().getShortName();
		} else {
			ownerId = "ADMIN";
		}

		if (this.slotDefParameters.getMode() == null
				|| this.slotDefParameters.getMode().equals("")) {
			return this.retrieve(this.slotDefParameters.isModel(), null,
					ownerId);
		} else if (this.slotDefParameters.getMode().equals(
				SlotDefType.PRIMARY.name())) {
			return this.retrieve(this.slotDefParameters.isModel(),
					SlotDefType.PRIMARY, ownerId);
		} else if (this.slotDefParameters.getMode().equals(
				SlotDefType.GENERAL.name())) {
			return this.retrieve(this.slotDefParameters.isModel(),
					SlotDefType.GENERAL, ownerId);
		} else if (this.slotDefParameters.getMode().equals(
				SlotDefType.DEPENDENT.name())) {
			return this.retrieve(this.slotDefParameters.isModel(),
					SlotDefType.DEPENDENT, ownerId);
		}
		return null;
	}

	public List<SlotDef> retrieveByType(Boolean areTemplates,
			String slotDefType, String ownerId) {
		SlotDefType type = SlotDefType.valueOf(slotDefType);
		return this.retrieve(areTemplates, type, ownerId);
	}

	// public boolean isModel() {
	// return model;
	// }
	//
	// public void setModel(boolean model) {
	// this.model = model;
	// }
	//
	// public String getMode() {
	// return mode;
	// }
	//
	// public void setMode(String mode) {
	// this.mode = mode;
	// }

}
