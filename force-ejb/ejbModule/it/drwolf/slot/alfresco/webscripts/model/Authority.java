package it.drwolf.slot.alfresco.webscripts.model;

public class Authority {

	private AuthorityType authorityType;

	private String shortName;

	private String fullName;

	private String displayName;

	private boolean isRootGroup;

	private boolean isAdminGroup;

	private String url;

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isRootGroup() {
		return isRootGroup;
	}

	public void setRootGroup(boolean rootGroup) {
		this.isRootGroup = rootGroup;
	}

	public boolean isAdminGroup() {
		return isAdminGroup;
	}

	public void setAdminGroup(boolean adminGroup) {
		this.isAdminGroup = adminGroup;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public AuthorityType getAuthorityType() {
		return authorityType;
	}

	public void setAuthorityType(AuthorityType authorityType) {
		this.authorityType = authorityType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authorityType == null) ? 0 : authorityType.hashCode());
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Authority))
			return false;
		Authority other = (Authority) obj;
		if (authorityType != other.authorityType)
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return authorityType + ":" + shortName;
	}

}
