package it.drwolf.slot.pagebeans.support;

public class FileDetails {

	private String objectId;

	private String name;

	private String creator;

	public FileDetails(String objectId, String name, String creator) {
		super();
		this.objectId = objectId;
		this.name = name;
		this.creator = creator;
	}

	public String getCreator() {
		return this.creator;
	}

	public String getName() {
		return this.name;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
}