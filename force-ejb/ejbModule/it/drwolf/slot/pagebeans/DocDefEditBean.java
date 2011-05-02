package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.custom.model.Aspect;
import it.drwolf.slot.application.CustomModel;
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
	private CustomModel customModel;

	private List<Aspect> aspects = new ArrayList<Aspect>();

	public List<Aspect> getAspects() {
		return aspects;
	}

	public void setAspects(List<Aspect> aspects) {
		this.aspects = aspects;
	}

	public void save() {
		for (Aspect aspect : aspects) {
			docDefHome.getInstance().addAspect(aspect.getName());
		}
		docDefHome.persist();
	}

	public void update() {
		Iterator<String> iDiterator = docDefHome.getInstance().getAspects()
				.iterator();
		// List<Aspect> aspectsAsObjects =
		// customModel.getSlotModel().getAspects();
		// List<Aspect> aspectsAsObjects = aspects;
		Iterator<Aspect> objectIterator = aspects.iterator();

		// elimina gli aspects tolti
		while (iDiterator.hasNext()) {
			String aspectId = iDiterator.next();
			boolean found = false;
			while (objectIterator.hasNext() && found == false) {
				Aspect aspect = objectIterator.next();
				if (aspect.getName().equals(aspectId)) {
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
			docDefHome.getInstance().addAspect(aspect.getName());
		}
		docDefHome.update();
	}

	@Create
	public void init() {
		List<Aspect> aspectsAsObjects = customModel.getSlotModel().getAspects();
		Iterator<Aspect> iterator = aspectsAsObjects.iterator();
		for (String aspectId : docDefHome.getInstance().getAspects()) {
			boolean found = false;
			while (iterator.hasNext() && found == false) {
				Aspect aspect = iterator.next();
				if (aspect.getName().equals(aspectId)) {
					aspects.add(aspect);
					found = true;
				}
			}
		}
	}

}
