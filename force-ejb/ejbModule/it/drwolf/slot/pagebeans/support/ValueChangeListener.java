package it.drwolf.slot.pagebeans.support;

import javax.faces.event.ValueChangeEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("valueChangeListener")
@Scope(ScopeType.EVENT)
public class ValueChangeListener {

	private Object previous;

	private Object value;

	public void listener(ValueChangeEvent value) {
		this.value = value.getNewValue();
		this.previous = value.getOldValue();
	}

	public Object getPrevious() {
		return previous;
	}

	public void setPrevious(Object previous) {
		this.previous = previous;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
