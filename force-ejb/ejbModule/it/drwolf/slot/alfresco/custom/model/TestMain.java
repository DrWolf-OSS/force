package it.drwolf.slot.alfresco.custom.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			loadModelFromInternalFolder();
			// encodeFilename();
			// timeStamp();
			// loadTest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void encodeFilename() {
		String origin = "nomefile.ext";
		int dotIndex = origin.lastIndexOf(".");
		String name = origin.substring(0, dotIndex);
		String extension = origin.substring(dotIndex);
		String newName = name + "_" + System.currentTimeMillis() + extension;

		int underscoreIndex = newName.lastIndexOf("_");
		String substring = newName.substring(0, underscoreIndex);
		String origin2 = substring.concat(extension);
		System.out.println("---> end");
	}

	private static void loadModelFromInternalFolder() {
		try {

			File source = new File("/home/drwolf/Scaricati/slotModel(5).xml");

			StringBuilder stringBuilder = new StringBuilder();
			String nl = System.getProperty("line.separator");
			Scanner scanner = new Scanner(source);

			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine() + nl);
			}
			String originalModel = stringBuilder.toString();

			String modifiedModel = "";

			String patternTxt = "(<constraint name=\"\\w+:\\w+\")";
			Pattern pattern = Pattern.compile(patternTxt);
			Matcher matcher = pattern.matcher(originalModel);
			// int count = 0;
			int begin = 0;
			// int gap = 0;
			String subfix = "";
			while (matcher.find()) {
				String group = matcher.group();

				String[] split = group.split("=");
				String idValue = " id=" + split[1];

				String prefix = originalModel.substring(begin, matcher.start());
				modifiedModel = modifiedModel.concat(prefix);

				subfix = originalModel.substring(matcher.end(),
						originalModel.length());

				String toAdd = group + idValue;
				modifiedModel = modifiedModel.concat(toAdd);

				begin = matcher.end();

				System.out.println(group);
				System.out.println("---> start: " + matcher.start());
				System.out.println("---> end: " + matcher.end());

				// count++;
			}
			modifiedModel = modifiedModel.concat(subfix);
			// System.out.println(count);
			System.out.println(modifiedModel);

			Strategy strategy = new CycleStrategy("id", "ref");
			Serializer serializer = new Persister(strategy);
			// Serializer serializer = new Persister();
			SlotModel slotModel = serializer.read(SlotModel.class,
					modifiedModel);
			//
			System.out.println("---> end");
			//
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void timeStamp() {
		System.out.println(System.nanoTime());
		Date d = new Date();
		System.out.println(d.getTime());
	}

	private static void loadTest() throws IOException, Exception {
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
	}

}
