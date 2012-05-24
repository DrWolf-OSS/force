package it.drwolf.force.entity;

import it.drwolf.force.enums.LogType;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Log")
public class Log implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4213001385306725096L;

	private Integer id;

	private String messages;

	private String stackTrace;

	private Date date;

	private String classe;

	private String method;

	private LogType type;

	private Azienda azienda;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Log other = (Log) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "azienda", nullable = true)
	public Azienda getAzienda() {
		return this.azienda;
	}

	@Column
	public String getClasse() {
		return this.classe;
	}

	@Column
	public Date getDate() {
		return this.date;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Column
	public String getMessages() {
		return this.messages;
	}

	@Column
	public String getMethod() {
		return this.method;
	}

	@Column
	@Lob
	public String getStackTrace() {
		return this.stackTrace;
	}

	@Column
	@Enumerated(EnumType.STRING)
	public LogType getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	public void setAzienda(Azienda azienda) {
		this.azienda = azienda;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public void setType(LogType type) {
		this.type = type;
	}

}
