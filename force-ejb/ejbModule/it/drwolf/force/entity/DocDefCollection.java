package it.drwolf.force.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.validator.NotNull;

@Entity
public class DocDefCollection {

	private Long id;

	private String name;

	private Integer min;

	private Integer max;

	private DocDef docDef;

	private SlotDef slotDef;

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

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	@OneToOne
	@NotNull
	public DocDef getDocDef() {
		return docDef;
	}

	public void setDocDef(DocDef docDef) {
		this.docDef = docDef;
	}

	@ManyToOne
	public SlotDef getSlotDef() {
		return slotDef;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	@Override
	public String toString() {
		return name + ":" + min + "," + max + ";" + docDef;
	}

}
