package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("docInstHome")
public class DocInstHome extends EntityHome<DocInst> {

	@In(create = true)
	DocInstCollectionHome docInstCollectionHome;

	public void setDocInstId(Long id) {
		setId(id);
	}

	public Long getDocInstId() {
		return (Long) getId();
	}

	@Override
	protected DocInst createInstance() {
		DocInst docInst = new DocInst();
		return docInst;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DocInstCollection docInstCollection = docInstCollectionHome
				.getDefinedInstance();
		if (docInstCollection != null) {
			getInstance().setDocInstCollection(docInstCollection);
		}
	}

	public boolean isWired() {
		return true;
	}

	public DocInst getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
