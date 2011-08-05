package it.drwolf.slot.exceptions;

import it.drwolf.slot.interfaces.DataInstance;

public class WrongDataTypeException extends Exception {

	private static final long serialVersionUID = -1980786572907891091L;

	private DataInstance dataInstance;

	public WrongDataTypeException() {
	}

	public WrongDataTypeException(DataInstance dataInstance) {
		super();
		this.dataInstance = dataInstance;
	}

	public DataInstance getDataInstance() {
		return dataInstance;
	}

}
