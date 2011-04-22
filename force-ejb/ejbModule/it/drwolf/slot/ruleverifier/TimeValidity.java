package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.IRuleVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeValidity implements IRuleVerifier {

	final private List<VerifierParameter> params = new ArrayList<VerifierParameter>();

	public TimeValidity() {
		params.add(new VerifierParameter("ExpirationDate", DataType.DATE));
		params.add(new VerifierParameter("DateToVerify", DataType.DATE));
	}

	public Object verify(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<VerifierParameter> getInParams() {
		return params;
	}

}
