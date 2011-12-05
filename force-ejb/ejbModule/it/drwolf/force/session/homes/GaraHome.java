package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Gara;
import it.drwolf.force.enums.TipoGara;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.Identity;

@Name("garaHome")
public class GaraHome extends EntityHome<Gara> {

	private static final long serialVersionUID = -1214123840587373460L;

	@In
	EntityManager entityManager;

	@In
	private AlfrescoUserIdentity alfrescoUserIdentity;

	public String attivaGara() {
		this.getInstance().setType(TipoGara.GESTITA.getNome());
		this.persist();
		return "OK";
	}

	@Override
	protected Gara createInstance() {
		Gara gara = new Gara();
		return gara;
	}

	public SlotDef getAssociatedSlotDef() {
		Identity identity = this.getIdentity();
		Iterator<SlotDef> iterator = this.getInstance().getSlotDefs()
				.iterator();

		String ownerId = null;
		if (identity.hasRole("ADMIN")
		// OR il cliente è un cliente che sta pagando
		) {
			ownerId = "ADMIN";
		} else {
			ownerId = this.alfrescoUserIdentity.getActiveGroup().getShortName();
		}

		while (iterator.hasNext()) {
			SlotDef slotDef = iterator.next();
			if (slotDef.getOwnerId().equals(ownerId)) {
				return slotDef;
			}
		}
		return null;
	}

	// Anche se ce ne sono più di uno restituisco l'ultimo istanziato
	@SuppressWarnings("unchecked")
	public SlotInst getAssociatedSlotInst() {
		SlotDef associatedSlotDef = this.getAssociatedSlotDef();
		if (associatedSlotDef != null) {
			List<SlotInst> resultList = this.entityManager
					.createQuery(
							"from SlotInst s where s.slotDef=:slotDef and s.ownerId=:ownerId order by s.id desc")
					.setParameter("slotDef", associatedSlotDef)
					.setParameter("ownerId",
							this.alfrescoUserIdentity.getMyOwnerId())
					.getResultList();
			if (!resultList.isEmpty()) {
				return resultList.get(0);
			}
		}
		return null;
	}

	public Gara getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public Integer getGaraId() {
		return (Integer) this.getId();
	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
	}

	@Override
	public String persist() {
		return super.persist();
	}

	public String scartaGara() {
		this.getInstance().setType(TipoGara.SCARTATA.getNome());
		this.persist();
		return "OK";
	}

	public void setGaraId(Integer id) {
		this.setId(id);
	}

	public void wire() {
		this.getInstance();
	}

}
