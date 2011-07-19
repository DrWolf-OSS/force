package it.drwolf.slot.digsig;

import java.io.InputStream;

public interface ICertsProvider {

	public interface ICertsProviderListener {

		public void changeResource(ICertsProvider provider);

	}

	public InputStream getResource();

	public void dispose();

	public void addListener(ICertsProviderListener listener);

	public void removeListener(ICertsProviderListener listener);

}
