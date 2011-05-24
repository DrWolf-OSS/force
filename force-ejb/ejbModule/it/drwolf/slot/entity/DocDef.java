package it.drwolf.slot.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CollectionOfElements;

@Entity
@Table(name = "DocDef", uniqueConstraints = { @UniqueConstraint(columnNames = "name") })
public class DocDef {

	private Long id;

	private String name;

	private Set<String> aspectIds = new HashSet<String>();

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

	@CollectionOfElements
	public Set<String> getAspectIds() {
		return aspectIds;
	}

	public void setAspectIds(Set<String> aspects) {
		this.aspectIds = aspects;
	}

	@Transient
	public void addAspectId(String aspectId) {
		this.aspectIds.add(aspectId);
	}

	@Override
	public String toString() {
		return this.name;
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result
	// + ((this.getName() == null) ? 0 : this.getName().hashCode());
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (!(obj instanceof DocDef))
	// return false;
	// DocDef other = (DocDef) obj;
	// if (this.getName() == null) {
	// if (other.getName() != null)
	// return false;
	// } else if (!this.getName().equals(other.getName()))
	// return false;
	// return true;
	// }

}
