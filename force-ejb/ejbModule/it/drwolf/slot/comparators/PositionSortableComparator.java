package it.drwolf.slot.comparators;

import it.drwolf.slot.interfaces.PositionSortable;

import java.util.Comparator;

public class PositionSortableComparator implements
		Comparator<PositionSortable> {

	public int compare(PositionSortable p1, PositionSortable p2) {
		if (p1.getPosition() == null && p2.getPosition() == null) {
			return 0;
		} else if (p1.getPosition() == null) {
			return 1;
		} else if (p2.getPosition() == null) {
			return -1;
		} else {
			return p1.getPosition().compareTo(p2.getPosition());
		}
	}

}
