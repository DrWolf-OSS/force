package it.drwolf.slot.digsig;

import java.util.ArrayList;
import java.util.List;

public abstract class CertsProviderNotifier implements ICertsProvider {

	protected List<ICertsProviderListener> listeners = new ArrayList<ICertsProviderListener>();

	protected void notifyResourceChange() {
		for (ICertsProviderListener l : listeners) {
			l.changeResource(this);
		}
	}

	public void addListener(ICertsProviderListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ICertsProviderListener listener) {
		listeners.remove(listener);
	}

}
