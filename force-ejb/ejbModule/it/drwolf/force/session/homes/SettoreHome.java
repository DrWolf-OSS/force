package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Settore;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("settoreHome")
public class SettoreHome extends EntityHome<Settore> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2940988629423359252L;

	@In
	EntityManager entityManager;

	@In
	private AlfrescoUserIdentity alfrescoUserIdentity;

	public SlotDef getAssociatedSlotDef() {
		if (this.getInstance() != null) {
			return this.getInstance().getAssociatedSlotDef();
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

	public Settore getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;

	}

	public Integer getSettoreId() {
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

	public void setSettoreId(Integer id) {
		this.setId(id);
	}

	public void wire() {
		this.getInstance();
	}

}
