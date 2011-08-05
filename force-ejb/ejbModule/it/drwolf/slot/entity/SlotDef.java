package it.drwolf.slot.entity;

import it.drwolf.slot.enums.SlotDefType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

@Entity
public class SlotDef {

	private Long id;

	private String name;

	private SlotDefType type = SlotDefType.GENERAL;

	private Set<DocDefCollection> docDefCollections = new HashSet<DocDefCollection>();

	private Set<Rule> rules = new HashSet<Rule>();

	private Set<PropertyDef> propertyDefs = new HashSet<PropertyDef>();

	private Set<EmbeddedProperty> embeddedProperties = new HashSet<EmbeddedProperty>();

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
	@OrderBy("name")
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

	@OrderBy("name")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "slotDef_id")
	public Set<PropertyDef> getPropertyDefs() {
		return propertyDefs;
	}

	public void setPropertyDefs(Set<PropertyDef> propertytDefs) {
		this.propertyDefs = propertytDefs;
	}

	@Transient
	public List<PropertyDef> getPropertyDefsAsList() {
		return new ArrayList<PropertyDef>(propertyDefs);
	}

	@Transient
	public void setPropertyDefsAsList(List<PropertyDef> propertyDefsAsList) {
		this.propertyDefs = new HashSet<PropertyDef>(propertyDefsAsList);
	}

	@Transient
	public List<DocDefCollection> getDocDefCollectionsAsList() {
		return new ArrayList<DocDefCollection>(docDefCollections);
	}

	@Transient
	public void setDocDefCollectionsAsList(
			List<DocDefCollection> docDefCollectionsAsList) {
		this.docDefCollections = new HashSet<DocDefCollection>(
				docDefCollectionsAsList);
	}

	@Transient
	public List<EmbeddedProperty> getEmbeddedPropertiesAsList() {
		return new ArrayList<EmbeddedProperty>(this.embeddedProperties);
	}

	@Transient
	public void setEmbeddedPropertiesAsList(
			List<EmbeddedProperty> embeddedPropertiesAsList) {
		this.embeddedProperties = new HashSet<EmbeddedProperty>(
				embeddedPropertiesAsList);
	}

	@Override
	public String toString() {
		return name;
	}

	@Enumerated(EnumType.STRING)
	public SlotDefType getType() {
		return type;
	}

	public void setType(SlotDefType type) {
		this.type = type;
	}

	@OrderBy("name")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "slotDef_id")
	public Set<EmbeddedProperty> getEmbeddedProperties() {
		return embeddedProperties;
	}

	public void setEmbeddedProperties(Set<EmbeddedProperty> embeddedProperties) {
		this.embeddedProperties = embeddedProperties;
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime
	// * result
	// + ((this.getDocDefCollections() == null) ? 0 : this
	// .getDocDefCollections().hashCode());
	// result = prime * result
	// + ((this.getId() == null) ? 0 : this.getId().hashCode());
	// result = prime * result
	// + ((this.getName() == null) ? 0 : this.getName().hashCode());
	// result = prime * result
	// + ((this.getType() == null) ? 0 : this.getType().hashCode());
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (!(obj instanceof SlotDef))
	// return false;
	// SlotDef other = (SlotDef) obj;
	// if (this.getDocDefCollections() == null) {
	// if (other.getDocDefCollections() != null)
	// return false;
	// } else if (!this.getDocDefCollections().equals(
	// other.getDocDefCollections()))
	// return false;
	// if (this.getId() == null) {
	// if (other.getId() != null)
	// return false;
	// } else if (!this.getId().equals(other.getId()))
	// return false;
	// if (this.getName() == null) {
	// if (other.getName() != null)
	// return false;
	// } else if (!this.getName().equals(other.getName()))
	// return false;
	// if (this.getType() != other.getType())
	// return false;
	// return true;
	// }

}
