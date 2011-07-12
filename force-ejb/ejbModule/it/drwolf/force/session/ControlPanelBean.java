package it.drwolf.force.session;

import it.drwolf.force.entity.Gara;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.pagebeans.SlotInstEditBean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("controlPanelBean")
@Scope(ScopeType.CONVERSATION)
public class ControlPanelBean {

	@In(create = true)
	private SlotInstEditBean slotInstEditBean;

	@In(create = true)
	private EntityManager entityManager;

	public List<DocInstCollection> getDocInstCollections() {
		return this.slotInstEditBean.getDocInstCollections().subList(0, 2);
	}

	public List<Gara> getSelectedGare() {
		ArrayList<Gara> elenco = (ArrayList<Gara>) this.entityManager
				.createQuery(" from Gara").getResultList();
		return elenco;
	}

	@Create
	public void init() {
		for (DocInstCollection docs : this.slotInstEditBean
				.getDocInstCollections()) {
			docs.getDocInsts();
		}
	}
}
