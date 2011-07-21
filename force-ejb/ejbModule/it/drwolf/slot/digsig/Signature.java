package it.drwolf.slot.digsig;

import java.util.Date;

public class Signature {

	public static final String VALIDITY = "dw:validity";
	public static final String EXPIRY = "dw:expiry";
	public static final String AUTHORITY = "dw:authority";
	public static final String SIGN = "dw:sign";
	public static final String CF = "dw:cf";

	private boolean validity;
	private Date expiry;
	private String authority;
	private String sign;
	private String cf;

	private String nodeRef;

	public Signature() {
	}

	public Signature(boolean validity, Date expiry, String authority,
			String sign, String cf, String nodeRef) {
		super();
		this.validity = validity;
		this.expiry = expiry;
		this.authority = authority;
		this.sign = sign;
		this.cf = cf;
		this.nodeRef = nodeRef;
	}

	public boolean isValidity() {
		return validity;
	}

	public void setValidity(boolean validity) {
		this.validity = validity;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getCf() {
		return cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

}
