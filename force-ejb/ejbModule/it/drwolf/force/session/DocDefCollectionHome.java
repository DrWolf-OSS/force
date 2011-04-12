package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("docDefCollectionHome")
public class DocDefCollectionHome extends EntityHome<DocDefCollection> {

	@In(create = true)
	DocDefHome docDefHome;
	@In(create = true)
	SlotDefHome slotDefHome;

	public void setDocDefCollectionId(Long id) {
		setId(id);
	}

	public Long getDocDefCollectionId() {
		return (Long) getId();
	}

	@Override
	protected DocDefCollection createInstance() {
		DocDefCollection docDefCollection = new DocDefCollection();
		return docDefCollection;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DocDef docDef = docDefHome.getDefinedInstance();
		if (docDef != null) {
			getInstance().setDocDef(docDef);
		}
		SlotDef slotDef = slotDefHome.getDefinedInstance();
		if (slotDef != null) {
			getInstance().setSlotDef(slotDef);
		}
	}

	public boolean isWired() {
		if (getInstance().getDocDef() == null)
			return false;
		return true;
	}

	public DocDefCollection getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
