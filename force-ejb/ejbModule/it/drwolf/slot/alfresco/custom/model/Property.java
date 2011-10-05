package it.drwolf.slot.alfresco.custom.model;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Property implements DataDefinition, Comparable<Property> {

	@Attribute
	private String name;

	@Element
	private String title;

	@Element
	private String type;

	@Element(required = false)
	private boolean mandatory;

	@Element(required = false)
	private boolean multiple;

	@Element(required = false, name = "default")
	private boolean defaultValue;

	@ElementList(required = false)
	private List<Constraint> constraints;

	private int position;

	// Parameter.name , Parameter.value
	// private Map<String, String> constraintParametersMap = new HashMap<String,
	// String>();

	public int compareTo(Property o) {
		if (o.getPosition() < this.getPosition()) {
			return 1;
		} else if (o.getPosition() > this.getPosition()) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Property)) {
			return false;
		}
		Property other = (Property) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	private Parameter getConstraintParameter(String name) {
		Iterator<Constraint> iterator = this.constraints.iterator();
		while (iterator.hasNext()) {
			Constraint constraint = iterator.next();
			if (constraint != null) {
				List<Parameter> parameters = constraint.getParameters();
				if (parameters != null) {
					Iterator<Parameter> iterator2 = parameters.iterator();
					while (iterator2.hasNext()) {
						Parameter parameter = iterator2.next();
						if (parameter.getName().equals(name)) {
							return parameter;
						}
					}
				}

			}
		}
		return null;
	}

	public List<Constraint> getConstraints() {
		return this.constraints;
	}

	public DataType getDataType() {
		if (this.type.equals("d:text")) {
			return DataType.STRING;
		} else if (this.type.equals("d:int")) {
			return DataType.INTEGER;
		} else if (this.type.equals("d:boolean")) {
			return DataType.BOOLEAN;
		} else if (this.type.equals("d:date")) {
			return DataType.DATE;
		} else {
			return null;
		}
	}

	public List<String> getDictionaryValues() {
		if (this.constraints != null) {
			Iterator<Constraint> iterator = this.constraints.iterator();
			while (iterator.hasNext()) {
				Constraint constraint = iterator.next();
				if (constraint.getType().equals(Constraint.LIST)) {
					List<Parameter> parameters = constraint.getParameters();
					Iterator<Parameter> iterator2 = parameters.iterator();
					while (iterator2.hasNext()) {
						Parameter parameter = iterator2.next();
						if (parameter.getName()
								.equals(Parameter.ALLOWED_VALUES)) {
							return parameter.getList();
						}
					}
				}
			}
		}
		return null;
	}

	//
	// /
	public String getLabel() {
		return this.title;
	}

	public String getName() {
		return this.name;
	}

	public int getPosition() {
		return this.position;
	}

	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	public boolean isDefaultValue() {
		return this.defaultValue;
	}

	public boolean isEditable() {
		return true;
	}

	public boolean isMandatory() {
		return this.mandatory;
	}

	public boolean isMultiple() {
		return this.multiple;
	}

	public boolean isRequired() {
		return this.mandatory;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.name + "," + this.type;
	}

	public void validate(FacesContext context, UIComponent component, Object obj)
			throws ValidatorException {
		if (this.getDataType().equals(DataType.STRING)) {
			if (!(obj instanceof String)) {
				FacesMessages.instance().add(Severity.ERROR,
						"Il valore dev'essere di tipo testo");
			}
			this.validateRegex((String) obj);
			this.validateLength((String) obj);
		}

		if (this.getDataType().equals(DataType.INTEGER)) {
			try {
				Integer value = new Integer((String) obj);
				this.validateMinMax(value);
			} catch (NumberFormatException e) {
				FacesMessage message = new FacesMessage();
				message.setSummary("Il valore deve essere un numero intero!");
				throw new ValidatorException(message);
			}
		}

	}

	private void validateLength(String value) {
		Parameter minLengthParam = this
				.getConstraintParameter(Parameter.MIN_LENGTH);
		Parameter maxLengthParam = this
				.getConstraintParameter(Parameter.MAX_LENGTH);

		// boolean failed = false;
		if (minLengthParam != null) {
			String _minl = minLengthParam.getValue();
			try {
				Integer minLength = new Integer(_minl);
				if (value.length() < minLength) {
					// failed = true;
					FacesMessage message = new FacesMessage();
					message.setSummary("La stringa deve essere lunga almeno "
							+ minLength + " caratteri");
					throw new ValidatorException(message);
				}
			} catch (NumberFormatException e) {
				FacesMessages
						.instance()
						.add(Severity.ERROR,
								"Errore nella definizione del parametro minLength sul modello");
			}
		}

		if (maxLengthParam != null) {
			String _maxl = maxLengthParam.getValue();
			try {
				Integer maxLength = new Integer(_maxl);
				if (value.length() > maxLength) {
					// failed = true;
					FacesMessage message = new FacesMessage();
					message.setSummary("La stringa deve essere lunga meno di "
							+ maxLength + " caratteri");
					throw new ValidatorException(message);
				}
			} catch (Exception e) {
				FacesMessages
						.instance()
						.add(Severity.ERROR,
								"Errore nella definizione del parametro maxLength sul modello");
			}
		}

	}

	private void validateMinMax(Integer value) {
		Parameter minParam = this.getConstraintParameter(Parameter.MIN_VALUE);
		Parameter maxParam = this.getConstraintParameter(Parameter.MAX_VALUE);

		if (minParam != null) {
			String _min = minParam.getValue();
			try {
				Integer min = new Integer(_min);
				if (value < min) {
					FacesMessage message = new FacesMessage();
					message.setSummary("Il valore deve essere maggiore di "
							+ min);
					throw new ValidatorException(message);
				}
			} catch (NumberFormatException e) {
				FacesMessages
						.instance()
						.add(Severity.ERROR,
								"Errore nella definizione del parametro minValue sul modello");
			}
		}

		if (maxParam != null) {
			String _max = maxParam.getValue();
			try {
				Integer max = new Integer(_max);
				if (value > max) {
					FacesMessage message = new FacesMessage();
					message.setSummary("Il valore deve essere minore di " + max);
					throw new ValidatorException(message);
				}
			} catch (Exception e) {
				FacesMessages
						.instance()
						.add(Severity.ERROR,
								"Errore nella definizione del parametro maxValue sul modello");
			}
		}
	}

	private void validateRegex(String obj) {
		Parameter regexParam = this
				.getConstraintParameter(Parameter.EXPRESSION);
		Parameter requiresMatchParam = this
				.getConstraintParameter(Parameter.REQUIRES_MATCH);

		if (regexParam != null) {
			String regex = regexParam.getValue();
			Boolean requiresMatch = Boolean.TRUE;
			if (requiresMatchParam != null) {
				String _rmp = requiresMatchParam.getValue();
				if (_rmp != null && !_rmp.equals("")) {
					requiresMatch = new Boolean(_rmp);
				}
			}
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(obj);
			if ((matcher.matches() ^ requiresMatch)) {
				FacesMessage message = new FacesMessage();
				message.setSummary("Valore non valido!");
				throw new ValidatorException(message);
			}
		}
	}
}
