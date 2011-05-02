package it.drwolf.slot.alfresco.custom.model;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			JarFile jar = new JarFile(
					"/home/drwolf/alfresco-3.4.d/tomcat/webapps/alfresco/WEB-INF/lib/it.drwolf.slot.alfresco.custom.jar");
			// Enumeration<JarEntry> entries = jar.entries();

			InputStream xmlSource = null;
			for (Enumeration entries = jar.entries(); entries.hasMoreElements();) {
				JarEntry jarElement = (JarEntry) entries.nextElement();
				String zipEntryName = jarElement.getName();
				System.out.println(zipEntryName);
				InputStream inputStream = jar.getInputStream(jarElement);
				if (zipEntryName
						.equals("it/drwolf/slot/alfresco/content/slotModel.xml")) {
					xmlSource = inputStream;
				}
			}

			Serializer serializer = new Persister();
			File source = new File(
					"/home/drwolf/git/force/it.drwolf.slot.alfresco.custom/src/java/it/drwolf/slot/alfresco/content/slotModel.xml");
			// SlotModel model = serializer.read(SlotModel.class, source);
			SlotModel model = serializer.read(SlotModel.class, xmlSource);
			System.out.println(model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
