package it.drwolf.force.session;

import it.drwolf.force.entity.ComunicazioneAziendaGara;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.entity.DocInst;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.pagebeans.SlotInstEditBean;
import it.drwolf.slot.pagebeans.support.FileContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.alfresco.cmis.client.AlfrescoDocument;
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

	@In(create = true)
	private CustomModelController customModelController;

	private ArrayList<DocInstCollection> inScadenza;

	private HashMap<Long, List<FileContainer>> inScadenzaFC;

	public List<DocInstCollection> getDocInstCollections() {
		for (DocInstCollection element : this.slotInstEditBean
				.getDocInstCollections()) {
			System.out.println(element);
		}
		return this.slotInstEditBean.getDocInstCollections();
	}

	public ArrayList<DocInstCollection> getInScadenza() {
		return this.inScadenza;
	}

	public HashMap<Long, List<FileContainer>> getInScadenzaFC() {
		return this.inScadenzaFC;
	}

	// questo metodo lo uso nel control panel per prendere le prime 5 gare
	// segnalate
	public List<ComunicazioneAziendaGara> getSelectedGare() {
		ArrayList<ComunicazioneAziendaGara> lista = new ArrayList<ComunicazioneAziendaGara>();
		ArrayList<ComunicazioneAziendaGara> elenco = (ArrayList<ComunicazioneAziendaGara>) this.entityManager
				.createQuery(
						"from ComunicazioneAziendaGara cag where cag.id.aziendaId = :a order by cag.gara.dataScadenza desc")
				.setParameter("a", this.userSession.getAzienda().getId())
				.setMaxResults(5).getResultList();
		lista.addAll(elenco);

		return lista;
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

		//
		this.slotInstEditBean.init();
		//

		// PALA
		// Il PrimarySlotInst potrebbe non essere ancora stato istanziato!
		if (this.userSession.getPrimarySlotInst() != null) {
			// mi tiro su tutti i documenti con slot:expirable
			// dato che potrei avere documenti nella cartella che hanno anche
			// l'aspect tmp
			// cioè documenti caricati ma non agganciati allo slot.
			ArrayList<String> ids = this.alfrescoWrapper
					.getDocumentIdsInFolderByAspect("slot:expirable",
							this.userSession.getPrimarySlotFolder().getId(),
							"slot:expirationDate", 0);
			this.inScadenza = new ArrayList<DocInstCollection>();
			this.inScadenzaFC = new HashMap<Long, List<FileContainer>>();
			HashSet<Property> properties = new HashSet<Property>();
			properties.add(this.customModelController
					.getProperty("slot:expirationDate"));
			for (DocInstCollection docs : this.slotInstEditBean
					.getDocInstCollections()) {
				DocInstCollection docInst = new DocInstCollection(
						docs.getSlotInst(), docs.getDocDefCollection());
				List<FileContainer> fileContainers = new ArrayList<FileContainer>();
				boolean added = false;
				// predo solo i documenti che hanno l'id associato sul db in
				// modom
				// da scartare quelli che hanno l'aspcet tmp
				for (DocInst doc : docs.getDocInsts()) {
					for (String id : ids) {
						if (doc.getNodeRef().equals(id)) {
							added = true;
							docInst.getDocInsts().add(doc);
							AlfrescoDocument ad = (AlfrescoDocument) this.alfrescoUserIdentity
									.getSession().getObject(doc.getNodeRef());
							FileContainer fc = new FileContainer(ad,
									properties, false);
							fileContainers.add(fc);
						}
					}
				}
				if (added) {
					this.inScadenza.add(docInst);
					this.inScadenzaFC.put(
							docInst.getDocDefCollection().getId(),
							fileContainers);
					// se la dimensione arriva a due esco
					if (this.inScadenza.size() == 2) {
						break;
					}
				}
			}
		}
		//
	}

	public void setInScadenza(ArrayList<DocInstCollection> inscadenza) {
		this.inScadenza = inscadenza;
	}

	public void setInScadenzaFC(HashMap<Long, List<FileContainer>> inScadenzaFC) {
		this.inScadenzaFC = inScadenzaFC;
	}
}
