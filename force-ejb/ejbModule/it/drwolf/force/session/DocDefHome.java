package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("docDefHome")
public class DocDefHome extends EntityHome<DocDef> {

	public void setDocDefId(Long id) {
		setId(id);
	}

	public Long getDocDefId() {
		return (Long) getId();
	}

	@Override
	protected DocDef createInstance() {
		DocDef docDef = new DocDef();
		return docDef;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public DocDef getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<PropertytDef> getPropertyDefs() {
		return getInstance() == null ? null : new ArrayList<PropertytDef>(
				getInstance().getPropertyDefs());
	}

}
