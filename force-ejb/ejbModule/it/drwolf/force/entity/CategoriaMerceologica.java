package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CategoriaMerceologica")
public class CategoriaMerceologica implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3573580041171154090L;

	private Integer id;

	private String categoria;

	private String type;

	@Column
	public String getCategoria() {
		return this.categoria;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Column
	public String getType() {
		return this.type;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

}
