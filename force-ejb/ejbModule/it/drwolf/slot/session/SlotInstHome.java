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

	public void setSlotInstId(Long id) {
		setId(id);
	}

	public Long getSlotInstId() {
		return (Long) getId();
	}

	@Override
	protected SlotInst createInstance() {
		SlotInst slotInst = new SlotInst();
		return slotInst;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		SlotDef slotDef = slotDefHome.getDefinedInstance();
		if (slotDef != null) {
			getInstance().setSlotDef(slotDef);
		}
	}

	public boolean isWired() {
		return true;
	}

	public SlotInst getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<DocInstCollection> getDocInstCollections() {
		return getInstance() == null ? null : new ArrayList<DocInstCollection>(
				getInstance().getDocInstCollections());
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

	public AlfrescoFolder retrieveOrCreateSlotFolder() {
		AlfrescoFolder slotFolder = retrieveSlotFolder();
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

}
