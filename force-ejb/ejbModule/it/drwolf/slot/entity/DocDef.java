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

	private Set<String> aspects = new HashSet<String>();

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
	public Set<String> getAspects() {
		return aspects;
	}

	public void setAspects(Set<String> aspects) {
		this.aspects = aspects;
	}

	@Transient
	public void addAspect(String aspectId) {
		this.aspects.add(aspectId);
	}

	@Override
	public String toString() {
		return this.name;
	}

}
