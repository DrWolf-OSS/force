package it.drwolf.slot.application;

import it.drwolf.slot.alfresco.custom.model.SlotModel;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Name("customModel")
@Scope(ScopeType.APPLICATION)
public class CustomModel {

	private SlotModel slotModel;

	@Create
	public void init() {
		loadModel();
	}

	private void loadModel() {
		if (slotModel == null) {
			try {
				JarFile jar;
				jar = new JarFile(
						"/home/drwolf/alfresco-3.4.d/tomcat/webapps/alfresco/WEB-INF/lib/it.drwolf.slot.alfresco.custom.jar");

				InputStream xmlSource = null;
				for (Enumeration entries = jar.entries(); entries
						.hasMoreElements();) {
					JarEntry jarElement = (JarEntry) entries.nextElement();
					String zipEntryName = jarElement.getName();
					if (zipEntryName
							.equals("it/drwolf/slot/alfresco/content/slotModel.xml")) {
						xmlSource = jar.getInputStream(jarElement);
					}
				}
				Serializer serializer = new Persister();
				this.slotModel = serializer.read(SlotModel.class, xmlSource);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public SlotModel getSlotModel() {
		return slotModel;
	}

	public void setSlotModel(SlotModel slotModel) {
		this.slotModel = slotModel;
	}

}
