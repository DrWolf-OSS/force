package it.drwolf.force.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "DocDef", uniqueConstraints = { @UniqueConstraint(columnNames = "name") })
public class DocDef {

	private Long id;

	private String name;

	private Set<PropertytDef> propertyDefs = new HashSet<PropertytDef>();

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
	@JoinColumn(name = "docDef_id")
	public Set<PropertytDef> getPropertyDefs() {
		return propertyDefs;
	}

	public void setPropertyDefs(Set<PropertytDef> propertyDefs) {
		this.propertyDefs = propertyDefs;
	}

}
