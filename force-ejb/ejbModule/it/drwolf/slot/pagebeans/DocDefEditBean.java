package it.drwolf.slot.pagebeans;

import it.drwolf.slot.enums.Aspect;
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

	private List<Aspect> aspects = new ArrayList<Aspect>();

	public List<Aspect> getAspects() {
		return aspects;
	}

	public void setAspects(List<Aspect> aspects) {
		this.aspects = aspects;
	}

	public void save() {
		for (Aspect aspect : aspects) {
			docDefHome.getInstance().addAspect(aspect.value());
		}
		docDefHome.persist();
	}

	public void update() {
		Iterator<String> iterator = docDefHome.getInstance().getAspects()
				.iterator();

		// elimina gli aspects tolti
		while (iterator.hasNext()) {
			String aspectId = iterator.next();
			if (!this.aspects.contains(Aspect.fromValue(aspectId))) {
				iterator.remove();
			}
		}

		for (Aspect aspect : aspects) {
			docDefHome.getInstance().addAspect(aspect.value());
		}
		docDefHome.update();
	}

	@Create
	public void init() {
		for (String aspectId : docDefHome.getInstance().getAspects()) {
			aspects.add(Aspect.fromValue(aspectId));
		}
	}

}
