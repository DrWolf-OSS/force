package it.drwolf.slot.digsig;

import java.util.Date;

public class Signature {

	private boolean validity;
	private Date notAfter;
	private Date notBefore;
	private String authority;
	private String sign;
	private String cf;

	private String nodeRef;

	public Signature() {
	}

	public Signature(boolean validity, Date expiry, Date notBefore,
			String authority, String sign, String cf, String nodeRef) {
		super();
		this.validity = validity;
		this.notAfter = expiry;
		this.notBefore = notBefore;
		this.authority = authority;
		this.sign = sign;
		this.cf = cf;
		this.nodeRef = nodeRef;
	}

	public String getAuthority() {
		return this.authority;
	}

	public String getCf() {
		return this.cf;
	}

	public String getNodeRef() {
		return this.nodeRef;
	}

	public Date getNotAfter() {
		return this.notAfter;
	}

	public Date getNotBefore() {
		return this.notBefore;
	}

	public String getSign() {
		return this.sign;
	}

	public boolean isValidity() {
		return this.validity;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public void setNotAfter(Date expiry) {
		this.notAfter = expiry;
	}

	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public void setValidity(boolean validity) {
		this.validity = validity;
	}

}
