package it.drwolf.slot.session;

import it.drwolf.slot.entity.*;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("docInstCollectionHome")
public class DocInstCollectionHome extends EntityHome<DocInstCollection> {

	@In(create = true)
	DocDefCollectionHome docDefCollectionHome;
	@In(create = true)
	SlotInstHome slotInstHome;

	public void setDocInstCollectionId(Long id) {
		setId(id);
	}

	public Long getDocInstCollectionId() {
		return (Long) getId();
	}

	@Override
	protected DocInstCollection createInstance() {
		DocInstCollection docInstCollection = new DocInstCollection();
		return docInstCollection;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DocDefCollection docDefCollection = docDefCollectionHome
				.getDefinedInstance();
		if (docDefCollection != null) {
			getInstance().setDocDefCollection(docDefCollection);
		}
		SlotInst slotInst = slotInstHome.getDefinedInstance();
		if (slotInst != null) {
			getInstance().setSlotInst(slotInst);
		}
	}

	public boolean isWired() {
		return true;
	}

	public DocInstCollection getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<DocInst> getDocInsts() {
		return getInstance() == null ? null : new ArrayList<DocInst>(
				getInstance().getDocInsts());
	}

}
