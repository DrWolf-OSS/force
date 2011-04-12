package it.drwolf.force.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class DocDef {

	private Long id;

	private String name;

	private Set<PropertytDef> propertyDefs;

	@Id
	@Enumerated
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

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn
	public Set<PropertytDef> getPropertyDefs() {
		return propertyDefs;
	}

	public void setPropertyDefs(Set<PropertytDef> propertyDefs) {
		this.propertyDefs = propertyDefs;
	}

}
