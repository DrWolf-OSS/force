package it.drwolf.slot.pagebeans;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.session.SlotDefHome;

import java.util.Iterator;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("positionSorterBean")
@Scope(ScopeType.CONVERSATION)
public class PositionSorterBean {

	@In
	private SlotDefHome slotDefHome;

	public static final String UP = "UP";
	public static final String DOWN = "DOWN";

	private void moveDocDefCollection(DocDefCollection docDefCollection,
			String direction) {
		Integer oldPosition = docDefCollection.getPosition();
		if (direction.equals(PositionSorterBean.UP)
				&& oldPosition.intValue() > 0) {
			docDefCollection.setPosition(oldPosition - 1);
		} else if (direction.equals(PositionSorterBean.DOWN)
				&& oldPosition.intValue() < this.slotDefHome.getInstance()
						.getDocDefCollections().size() - 1) {
			docDefCollection.setPosition(oldPosition + 1);
		} else {
			return;
		}

		if (docDefCollection instanceof DocDefCollection) {
			Iterator<DocDefCollection> iterator = this.slotDefHome
					.getInstance().getDocDefCollections().iterator();
			while (iterator.hasNext()) {
				DocDefCollection ddc = iterator.next();
				if (ddc.getPosition().intValue() == (docDefCollection
						.getPosition().intValue())
						&& !ddc.equals(docDefCollection)) {
					ddc.setPosition(oldPosition);
					return;
				}
			}
		}
	}

	public void moveDocDefCollectionDown(DocDefCollection sortable) {
		this.moveDocDefCollection(sortable, PositionSorterBean.DOWN);
	}

	public void moveDocDefCollectionUp(DocDefCollection sortable) {
		this.moveDocDefCollection(sortable, PositionSorterBean.UP);
	}

	private void movePropertyDef(PropertyDef propertyDef, String direction) {
		Integer oldPosition = propertyDef.getPosition();
		if (direction.equals(PositionSorterBean.UP)
				&& oldPosition.intValue() > 0) {
			propertyDef.setPosition(oldPosition - 1);
		} else if (direction.equals(PositionSorterBean.DOWN)
				&& oldPosition < this.slotDefHome.getInstance()
						.getPropertyDefs().size() - 1) {
			propertyDef.setPosition(oldPosition + 1);
		} else {
			return;
		}

		if (propertyDef instanceof PropertyDef) {
			Iterator<PropertyDef> iterator = this.slotDefHome.getInstance()
					.getPropertyDefs().iterator();
			while (iterator.hasNext()) {
				PropertyDef pd = iterator.next();
				if (pd.getPosition().intValue() == (propertyDef.getPosition()
						.intValue()) && !pd.equals(propertyDef)) {
					pd.setPosition(oldPosition);
					return;
				}
			}
		}
	}

	public void movePropertyDefDown(PropertyDef sortable) {
		this.movePropertyDef(sortable, PositionSorterBean.DOWN);
	}

	public void movePropertyDefUp(PropertyDef sortable) {
		this.movePropertyDef(sortable, PositionSorterBean.UP);
	}

}
