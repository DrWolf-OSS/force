package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.custom.model.Aspect;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.session.DocDefHome;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("docDefEditBean")
@Scope(ScopeType.CONVERSATION)
public class DocDefEditBean {

	@In(create = true)
	private DocDefHome docDefHome;

	@In(create = true)
	private CustomModelController customModelController;

	private List<Aspect> aspects = new ArrayList<Aspect>();

	public List<Aspect> getAspects() {
		return aspects;
	}

	public void setAspects(List<Aspect> aspects) {
		this.aspects = aspects;
	}

	public void save() {
		for (Aspect aspect : aspects) {
			docDefHome.getInstance().addAspectId(aspect.getId());
		}
		docDefHome.persist();
	}

	public void update() {
		Iterator<String> iDiterator = docDefHome.getInstance().getAspectIds()
				.iterator();
		Iterator<Aspect> objectIterator = aspects.iterator();

		// elimina gli id degli aspects tolti
		while (iDiterator.hasNext()) {
			String aspectId = iDiterator.next();
			boolean found = false;
			while (objectIterator.hasNext() && found == false) {
				Aspect aspect = objectIterator.next();
				if (aspect.getId().equals(aspectId)) {
					// aspects.add(aspect);
					found = true;
				}
			}
			if (found == false) {
				iDiterator.remove();
			}
			// if (!this.aspects.contains(Aspect.fromValue(aspectId))) {
			// iDiterator.remove();
			// }
		}

		for (Aspect aspect : aspects) {
			docDefHome.getInstance().addAspectId(aspect.getId());
		}
		docDefHome.update();
	}

	@Create
	public void init() {
		List<Aspect> aspectsAsObjects = customModelController.getSlotModel().getAspects();
		Iterator<Aspect> iterator = aspectsAsObjects.iterator();
		for (String aspectId : docDefHome.getInstance().getAspectIds()) {
			boolean found = false;
			while (iterator.hasNext() && found == false) {
				Aspect aspect = iterator.next();
				if (aspect.getId().equals(aspectId)) {
					aspects.add(aspect);
					found = true;
				}
			}
		}
	}

}
