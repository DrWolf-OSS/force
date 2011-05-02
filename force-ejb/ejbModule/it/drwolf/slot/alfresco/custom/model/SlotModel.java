package it.drwolf.slot.alfresco.custom.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(name = "model")
@Namespace(reference = "http://www.alfresco.org/model/dictionary/1.0")
public class SlotModel {

	@Attribute
	private String name;

	@Element
	private String description;

	@Element
	private String author;

	@Element
	private String version;

	@ElementList
	private List<Import> imports;

	@ElementList
	private List<it.drwolf.slot.alfresco.custom.model.Namespace> namespaces;

	@ElementList
	private List<Aspect> aspects;

	public SlotModel() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<Import> getImports() {
		return imports;
	}

	public void setImports(List<Import> imports) {
		this.imports = imports;
	}

	public List<it.drwolf.slot.alfresco.custom.model.Namespace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(
			List<it.drwolf.slot.alfresco.custom.model.Namespace> namespaces) {
		this.namespaces = namespaces;
	}

	public List<Aspect> getAspects() {
		return aspects;
	}

	public void setAspects(List<Aspect> aspects) {
		this.aspects = aspects;
	}

}
