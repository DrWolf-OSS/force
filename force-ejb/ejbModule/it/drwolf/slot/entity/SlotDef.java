package it.drwolf.slot.entity;

import it.drwolf.slot.comparators.PositionSortableComparator;
import it.drwolf.slot.entity.listeners.SlotDefListener;
import it.drwolf.slot.enums.SlotDefSatus;
import it.drwolf.slot.enums.SlotDefType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

@Entity
@EntityListeners(value = SlotDefListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public class SlotDef {

	private Long id;

	private String name;

	private SlotDefType type = SlotDefType.GENERAL;

	private Set<DocDefCollection> docDefCollections = new HashSet<DocDefCollection>();

	private Set<Rule> rules = new HashSet<Rule>();

	private Set<PropertyDef> propertyDefs = new HashSet<PropertyDef>();

	private Set<EmbeddedProperty> embeddedProperties = new HashSet<EmbeddedProperty>();

	private boolean template = Boolean.FALSE;

	private Set<DependentSlotDef> dependentSlotDefs = new HashSet<DependentSlotDef>();

	private SlotDefSatus status = SlotDefSatus.UNKNOWN;

	private String ownerId;

	private boolean pubblicato = Boolean.FALSE;

	@OneToMany(mappedBy = "parentSlotDef", cascade = CascadeType.ALL)
	public Set<DependentSlotDef> getDependentSlotDefs() {
		return this.dependentSlotDefs;
	}

	@Transient
	public List<DependentSlotDef> getDependentSlotDefsAsList() {
		return new ArrayList<DependentSlotDef>(this.getDependentSlotDefs());
	}

	@OneToMany(mappedBy = "slotDef", cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OrderBy("position")
	public Set<DocDefCollection> getDocDefCollections() {
		return this.docDefCollections;
	}

	@Transient
	public List<DocDefCollection> getDocDefCollectionsAsList() {
		ArrayList<DocDefCollection> list = new ArrayList<DocDefCollection>(
				this.docDefCollections);
		Collections.sort(list, new PositionSortableComparator());
		return list;
	}

	@OrderBy("position")
	@OneToMany(cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@JoinColumn(name = "slotDef_id")
	public Set<EmbeddedProperty> getEmbeddedProperties() {
		return this.embeddedProperties;
	}

	@Transient
	public List<EmbeddedProperty> getEmbeddedPropertiesAsList() {
		ArrayList<EmbeddedProperty> list = new ArrayList<EmbeddedProperty>(
				this.embeddedProperties);
		Collections.sort(list, new PositionSortableComparator());
		return list;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getOwnerId() {
		return this.ownerId;
	}

	@OrderBy("position")
	@OneToMany(cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@JoinColumn(name = "slotDef_id")
	public Set<PropertyDef> getPropertyDefs() {
		return this.propertyDefs;
	}

	@Transient
	public List<PropertyDef> getPropertyDefsAsList() {
		ArrayList<PropertyDef> list = new ArrayList<PropertyDef>(
				this.propertyDefs);
		Collections.sort(list, new PositionSortableComparator());
		return list;
	}

	@Transient
	@SuppressWarnings("unchecked")
	public List<SlotInst> getReferencedSlotInsts() {
		EntityManager entityManager = (EntityManager) org.jboss.seam.Component
				.getInstance("entityManager");
		if (this.getId() != null) {
			List<SlotInst> resultList = entityManager
					.createQuery("from SlotInst s where s.slotDef=:slotDef")
					.setParameter("slotDef", this).getResultList();
			if (resultList != null) {
				return resultList;
			}
		}
		return new ArrayList<SlotInst>();
	}

	@OneToMany(mappedBy = "slotDef", cascade = CascadeType.ALL)
	public Set<Rule> getRules() {
		return this.rules;
	}

	@Transient
	public List<Rule> getRulesAsList() {
		return new ArrayList<Rule>(this.rules);
	}

	@Enumerated(EnumType.STRING)
	public SlotDefSatus getStatus() {
		return this.status;
	}

	@Enumerated(EnumType.STRING)
	public SlotDefType getType() {
		return this.type;
	}

	public boolean isPubblicato() {
		return this.pubblicato;
	}

	@Transient
	@SuppressWarnings("unchecked")
	public boolean isReferenced() {
		EntityManager entityManager = (EntityManager) org.jboss.seam.Component
				.getInstance("entityManager");
		if (this.getId() != null) {
			List<SlotInst> resultList = entityManager
					.createQuery(
							"select id from SlotInst s where s.slotDef=:slotDef")
					.setParameter("slotDef", this).setMaxResults(1)
					.getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean isTemplate() {
		return this.template;
	}

	@Transient
	public DocDefCollection retrieveDocDefCollectionByName(String name) {
		Iterator<DocDefCollection> iterator = this.getDocDefCollections()
				.iterator();
		while (iterator.hasNext()) {
			DocDefCollection docDefCollection = iterator.next();
			if (docDefCollection.getName().equals(name)) {
				return docDefCollection;
			}
		}
		return null;
	}

	@Transient
	public EmbeddedProperty retrieveEmbeddedPropertyByName(String name) {
		Iterator<EmbeddedProperty> iterator = this.getEmbeddedProperties()
				.iterator();
		while (iterator.hasNext()) {
			EmbeddedProperty embeddedProperty = iterator.next();
			if (embeddedProperty.getName().equals(name)) {
				return embeddedProperty;
			}
		}
		return null;
	}

	@Transient
	public PropertyDef retrievePropertyDefById(Long id) {
		Iterator<PropertyDef> iterator = this.getPropertyDefs().iterator();
		while (iterator.hasNext()) {
			PropertyDef propertyDef = iterator.next();
			if (propertyDef.getId() == id) {
				return propertyDef;
			}
		}
		return null;
	}

	@Transient
	public PropertyDef retrievePropertyDefByName(String name) {
		Iterator<PropertyDef> iterator = this.getPropertyDefs().iterator();
		while (iterator.hasNext()) {
			PropertyDef propertyDef = iterator.next();
			if (propertyDef.getName().equals(name)) {
				return propertyDef;
			}
		}
		return null;
	}

	public void setDependentSlotDefs(Set<DependentSlotDef> dependentSlotDefs) {
		this.dependentSlotDefs = dependentSlotDefs;
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

	@Transient
	public void setDependentSlotDefsAsList(
			List<DependentSlotDef> dependentSlotDefsAsList) {
		this.setDependentSlotDefs(new HashSet<DependentSlotDef>(
				dependentSlotDefsAsList));
	}

	public void setDocDefCollections(Set<DocDefCollection> docDefCollections) {
		this.docDefCollections = docDefCollections;
	}

	@Transient
	public void setDocDefCollectionsAsList(
			List<DocDefCollection> docDefCollectionsAsList) {
		this.docDefCollections = new HashSet<DocDefCollection>(
				docDefCollectionsAsList);
	}

	public void setEmbeddedProperties(Set<EmbeddedProperty> embeddedProperties) {
		this.embeddedProperties = embeddedProperties;
	}

	@Transient
	public void setEmbeddedPropertiesAsList(
			List<EmbeddedProperty> embeddedPropertiesAsList) {
		this.embeddedProperties = new HashSet<EmbeddedProperty>(
				embeddedPropertiesAsList);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public void setPropertyDefs(Set<PropertyDef> propertytDefs) {
		this.propertyDefs = propertytDefs;
	}

	@Transient
	public void setPropertyDefsAsList(List<PropertyDef> propertyDefsAsList) {
		this.propertyDefs = new HashSet<PropertyDef>(propertyDefsAsList);
	}

	public void setPubblicato(boolean pubblicato) {
		this.pubblicato = pubblicato;
	}

	public void setRules(Set<Rule> rules) {
		this.rules = rules;
	}

	@Transient
	public void setRulesAsList(List<Rule> rules) {
		this.setRules(new HashSet<Rule>(rules));
	}

	public void setStatus(SlotDefSatus status) {
		this.status = status;
	}

	public void setTemplate(boolean template) {
		this.template = template;
	}

	public void setType(SlotDefType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
