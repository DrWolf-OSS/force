package it.drwolf.slot.entity.listeners;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.enums.CollectionQuantifier;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class DocDefCollectionListener {

	@PrePersist
	@PreUpdate
	public void setMinMax(DocDefCollection docDefCollection) {
		CollectionQuantifier quantifier = docDefCollection.getQuantifier();
		if (quantifier != null) {
			switch (quantifier) {
			case ANY_OR_ONE:
				docDefCollection.setMin(null);
				docDefCollection.setMax(new Integer(1));
				break;

			case ANY_OR_MORE:
				docDefCollection.setMin(null);
				docDefCollection.setMax(null);
				break;

			case ONLY_ONE:
				docDefCollection.setMin(new Integer(1));
				docDefCollection.setMax(new Integer(1));
				break;

			case ONE_OR_MORE:
				docDefCollection.setMin(new Integer(1));
				docDefCollection.setMax(null);
				break;

			default:
				break;
			}
		}
	}

	@PostLoad
	public void setQuantifier(DocDefCollection docDefCollection) {
		if (docDefCollection.getMin() == null
				&& docDefCollection.getMax() != null
				&& docDefCollection.getMax() == 1) {
			docDefCollection.setQuantifier(CollectionQuantifier.ANY_OR_ONE);
		} else if (docDefCollection.getMin() == null
				&& docDefCollection.getMax() == null) {
			docDefCollection.setQuantifier(CollectionQuantifier.ANY_OR_MORE);
		} else if (docDefCollection.getMin() != null
				&& docDefCollection.getMin() == 1
				&& docDefCollection.getMax() != null
				&& docDefCollection.getMax() == 1) {
			docDefCollection.setQuantifier(CollectionQuantifier.ONLY_ONE);
		} else if (docDefCollection.getMin() != null
				&& docDefCollection.getMin() == 1
				&& docDefCollection.getMax() == null) {
			docDefCollection.setQuantifier(CollectionQuantifier.ONE_OR_MORE);
		} else {
			docDefCollection.setQuantifier(null);
		}
	}
}
