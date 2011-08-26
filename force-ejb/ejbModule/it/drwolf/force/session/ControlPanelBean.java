package it.drwolf.force.session;

import it.drwolf.force.entity.Gara;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.entity.DocInst;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.SlotInst;
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

	@In
	private UserSession userSession;

	@In(create = true)
	private EntityManager entityManager;

	@In(create = true)
	AlfrescoUserIdentity alfrescoUserIdentity;

	@In(create = true)
	private AlfrescoWrapper alfrescoWrapper;

	public List<DocInstCollection> getDocInstCollections() {
		for (DocInstCollection element : this.slotInstEditBean
				.getDocInstCollections()) {
			System.out.println(element);
		}
		return this.slotInstEditBean.getDocInstCollections();
	}

	public List<Gara> getSelectedGare() {
		ArrayList<Gara> elenco = (ArrayList<Gara>) this.entityManager
				.createQuery(
						" select distinct  g from Azienda a join a.categorieMerceologiche cm join cm.gare g where a.id = :a")
				.setParameter("a", this.userSession.getAziendaId())
				.getResultList();
		return elenco;
	}

	public List<SlotInst> getSlotInst() {
		ArrayList<SlotInst> elenco = (ArrayList<SlotInst>) this.entityManager
				.createQuery(
						"from SlotInst s where s.slotDef.type='GENERAL' and s.ownerId = :oid")
				.setParameter(
						"oid",
						this.alfrescoUserIdentity.getActiveGroup()
								.getShortName()).getResultList();
		return elenco;
	}

	@Create
	public void init() {
		// all'inizializzazione mi faccio dare i primi n documenti in scadenza
		// per farlo procedo nel seguente modo:
		// prelevo con una query cmis tutti i documenti che sono nella cartella
		// dello slot primary che sono scabili
		ArrayList<String> ids = this.alfrescoWrapper
				.getDocumentIdsInFolderByAspect("slot:expirable",
						this.userSession.getPrimarySlotFolder().getId(),
						"slot:expirationDate", 2);
		for (DocInstCollection docs : this.slotInstEditBean
				.getDocInstCollections()) {
			for (DocInst doc : docs.getDocInsts()) {
				for (String id : ids) {
					if (doc.getNodeRef().equals(id)) {
						// documento prossimo alla scadenza
						System.out.println(doc);
					}
				}
			}
		}
	}
}
