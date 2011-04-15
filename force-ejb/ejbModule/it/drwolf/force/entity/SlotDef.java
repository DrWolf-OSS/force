package it.drwolf.force.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class SlotDef {

	private Long id;

	private String name;

	private Set<DocDefCollection> docDefCollections = new HashSet<DocDefCollection>();

	private Set<Rule> rules = new HashSet<Rule>();

	private Set<PropertyDef> propertyDefs = new HashSet<PropertyDef>();

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "slotDef", cascade = CascadeType.ALL)
	public Set<DocDefCollection> getDocDefCollections() {
		return docDefCollections;
	}

	public void setDocDefCollections(Set<DocDefCollection> docDefCollections) {
		this.docDefCollections = docDefCollections;
	}

	@OneToMany(mappedBy = "slotDef", cascade = CascadeType.ALL)
	public Set<Rule> getRules() {
		return rules;
	}

	public void setRules(Set<Rule> rules) {
		this.rules = rules;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "slotDef_id")
	public Set<PropertyDef> getPropertyDefs() {
		return propertyDefs;
	}

	public void setPropertyDefs(Set<PropertyDef> propertytDefs) {
		this.propertyDefs = propertytDefs;
	}

	@Transient
	public List<PropertyDef> getPropertiesAsList() {
		return new ArrayList<PropertyDef>(propertyDefs);
	}

	@Transient
	public List<DocDefCollection> getDocDefCollectionsAsList() {
		return new ArrayList<DocDefCollection>(docDefCollections);
	}

	@Override
	public String toString() {
		return name;
	}

}
