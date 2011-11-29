package it.drwolf.slot.session;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("slotInstHome")
public class SlotInstHome extends EntityHome<SlotInst> {

	@In(create = true)
	SlotDefHome slotDefHome;

	@In(create = true)
	AlfrescoUserIdentity alfrescoUserIdentity;

	@In(create = true)
	AlfrescoWrapper alfrescoWrapper;

	@In(create = true)
	Preferences preferences;

	@Override
	protected SlotInst createInstance() {
		SlotInst slotInst = new SlotInst();
		return slotInst;
	}

	public SlotInst getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public List<DocInstCollection> getDocInstCollections() {
		return this.getInstance() == null ? null
				: new ArrayList<DocInstCollection>(this.getInstance()
						.getDocInstCollections());
	}

	public Long getSlotInstId() {
		return (Long) this.getId();
	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
	}

	public AlfrescoFolder retrieveOrCreateSlotFolder() {
		AlfrescoFolder slotFolder = this.retrieveSlotFolder();
		if (slotFolder == null) {
			AlfrescoFolder groupFolder = this.alfrescoWrapper
					.findOrCreateFolder(this.preferences
							.getValue(PreferenceKey.FORCE_GROUPS_PATH.name()),
							this.alfrescoUserIdentity.getActiveGroup()
									.getShortName());
			slotFolder = this.alfrescoWrapper.findOrCreateFolder(
					groupFolder,
					this.slotDefHome.getInstance().getId()
							+ " "
							+ AlfrescoWrapper.normalizeFolderName(
									this.slotDefHome.getInstance().getName(),
									AlfrescoWrapper.LENGHT_LIMIT,
									AlfrescoWrapper.SPACER));
			this.getInstance().setNodeRef(slotFolder.getId());
		}
		return slotFolder;
	}

	public AlfrescoFolder retrieveSlotFolder() {
		AlfrescoFolder slotFolder = null;
		if ((this.getInstance() != null)
				&& (this.getInstance().getNodeRef() != null)
				&& !this.getInstance().getNodeRef().equals("")) {
			try {
				slotFolder = (AlfrescoFolder) this.alfrescoUserIdentity
						.getSession()
						.getObject(this.getInstance().getNodeRef());
			} catch (CmisObjectNotFoundException e) {
				e.printStackTrace();
			}
		}
		return slotFolder;
	}

	// Il codice commentato serve per controllare in modo programmatico che lo
	// SlotDef nella home sia quello giusto (quello referenziato dallo SlotInst)
	// Nel caso scommentare anche il corrispettivo in SlotDefHome
	public void setSlotInstId(Long id) {
		this.setId(id);
		// if (this.isIdDefined()) {
		// this.slotDefHome.setSlotDefId(this.getInstance().getSlotDef()
		// .getId());
		// }
	}

	public void wire() {
		this.getInstance();
		SlotDef slotDef = this.slotDefHome.getDefinedInstance();
		if (slotDef != null) {
			this.getInstance().setSlotDef(slotDef);
		}
	}

}
