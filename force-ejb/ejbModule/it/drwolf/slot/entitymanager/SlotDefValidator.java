package it.drwolf.slot.entitymanager;

import it.drwolf.slot.entity.DependentSlotDef;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.enums.SlotDefSatus;
import it.drwolf.slot.interfaces.DataDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

public class SlotDefValidator {

	private SlotDef slotDef;

	public SlotDefValidator() {
	}

	public SlotDefValidator(SlotDef slotDef) {
		super();
		this.slotDef = slotDef;
	}

	private boolean checkCollectionReferences(boolean displayMessages,
			SlotDef slotDef) {
		boolean result = true;

		for (DocDefCollection collection : slotDef.getDocDefCollections()) {
			PropertyDef conditionalPropertyDef = collection
					.getConditionalPropertyDef();
			if (conditionalPropertyDef != null
					&& collection.getConditionalPropertyInst().getValue() == null) {
				result = false;
				if (displayMessages) {
					FacesMessages
							.instance()
							.add(Severity.ERROR,
									"La richiesta \""
											+ collection.getName()
											+ "\" vincolata dall'informazione \""
											+ conditionalPropertyDef.getLabel()
											+ "\" non ha il valore del vincolo valorizzato");
				}
			}
		}
		if (!result && displayMessages) {
			FacesMessages
					.instance()
					.add(Severity.ERROR,
							"Non possono esserci richieste vincolate con il loro vincolo non valorizzato!");
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	private boolean checkEmbeddedPropertyValues(boolean displayMessages,
			SlotDef slotDef) {
		boolean passed = true;
		for (EmbeddedProperty embeddedProperty : slotDef
				.getEmbeddedProperties()) {
			if ((!embeddedProperty.isMultiple() && (embeddedProperty.getValue() == null || embeddedProperty
					.getValue().equals("")))
					|| (embeddedProperty.isMultiple() && ((Set) embeddedProperty
							.getValue()).isEmpty())) {
				if (displayMessages) {
					FacesMessages.instance().add(
							Severity.ERROR,
							"L'informazione \"" + embeddedProperty.getLabel()
									+ "\" non è stata valorizzata");
				}
				passed = false;
			}
		}
		if (!passed) {
			if (displayMessages) {
				FacesMessages
						.instance()
						.add(Severity.ERROR,
								"Non possono esserci informazioni sulla gara non valorizzate!");
			}
		}
		return passed;
	}

	private boolean checkNames(boolean displayMessages, SlotDef slotDef) {
		Set<String> differentNames = new HashSet<String>();
		boolean result = true;

		List<DataDefinition> allProperties = new ArrayList<DataDefinition>();
		allProperties.addAll(slotDef.getPropertyDefsAsList());
		allProperties.addAll(slotDef.getEmbeddedPropertiesAsList());

		for (DataDefinition p : allProperties) {
			differentNames.add(p.getLabel());
		}
		if (differentNames.size() < allProperties.size()) {
			result = false;
			if (displayMessages) {
				FacesMessages.instance().add(Severity.ERROR,
						"Non possono esserci property con lo stesso nome");
			}
		}

		differentNames.clear();
		for (DocDefCollection d : slotDef.getDocDefCollectionsAsList()) {
			differentNames.add(d.getName());
		}
		if (differentNames.size() < slotDef.getDocDefCollectionsAsList().size()) {
			result = false;
			if (displayMessages) {
				FacesMessages.instance().add(Severity.ERROR,
						"Non possono esserci collections con lo stesso nome");
			}
		}

		return result;
	}

	public SlotDef getSlotDef() {
		return this.slotDef;
	}

	private boolean ricorsiveValidateSingleSlot(boolean displayMessages,
			SlotDef slotDef) {

		// if (slotDef instanceof DependentSlotDef) {
		// this.ricorsiveValidateSingleSlot(false,
		// ((DependentSlotDef) slotDef).getParentSlotDef());
		// }

		boolean result = true;
		// boolean singleSlotResult = true;
		if (slotDef.getDependentSlotDefs() != null
				&& !slotDef.getDependentSlotDefs().isEmpty()) {
			for (SlotDef dependentSlotDef : slotDef.getDependentSlotDefs()) {
				boolean dependentResult = this.ricorsiveValidateSingleSlot(
						false, dependentSlotDef);
				if (!dependentResult) {
					result = false;
					FacesMessages.instance().add(
							Severity.ERROR,
							"La sottobusta " + dependentSlotDef.getName()
									+ " non è valida. Editarla singolarmente");
				}
			}
		}
		boolean names = this.checkNames(displayMessages, slotDef);
		boolean references = this.checkCollectionReferences(displayMessages,
				slotDef);
		boolean embeddedValues = true;
		if (!slotDef.isTemplate()) {
			embeddedValues = this.checkEmbeddedPropertyValues(displayMessages,
					slotDef);
		}

		//
		// if (names && references && embeddedValues) {
		// singleSlotResult = true;
		// } else {
		// singleSlotResult = false;
		// }
		//

		if (names && references && embeddedValues && result) {
			// VALIDO
			slotDef.setStatus(SlotDefSatus.VALID);
			return true;
		} else {
			// NON VALIDO
			slotDef.setStatus(SlotDefSatus.INVALID);
			return false;
		}

		// return singleSlotResult;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	public boolean validate() {
		if (this.slotDef instanceof DependentSlotDef) {
			return this.ricorsiveValidateSingleSlot(true,
					((DependentSlotDef) this.slotDef).getParentSlotDef());
		}
		return this.ricorsiveValidateSingleSlot(true, this.slotDef);
	}

	// /**
	// *
	// * @param displayMessages
	// * @return
	// */
	// public boolean validateAll(boolean displayMessages) {
	// boolean names = this.checkNames(displayMessages);
	// boolean references = this.checkCollectionReferences(displayMessages);
	// boolean embeddedValues = true;
	// if (!this.slotDef.isTemplate()) {
	// embeddedValues = this.checkEmbeddedPropertyValues(displayMessages);
	// }
	// if (names && references && embeddedValues) {
	// // VALIDO
	// boolean validated = true;
	// for (SlotDef dependent : this.slotDef.getDependentSlotDefs()) {
	// SlotDefValidator dependentValidator = new SlotDefValidator(
	// dependent);
	// if (!dependentValidator.validate(false)) {
	// validated = false;
	// FacesMessages.instance().add(
	// Severity.ERROR,
	// "La sottobusta " + dependent.getName()
	// + " non è valida. Editarla singolarmente");
	// }
	// }
	// return validated;
	// } else {
	// // NON VALIDO
	// return false;
	// }
	// }

	// private boolean validateSingleSlot(boolean displayMessages, SlotDef
	// slotDef) {
	// boolean names = this.checkNames(displayMessages);
	// boolean references = this.checkCollectionReferences(displayMessages);
	// boolean embeddedValues = true;
	// if (!slotDef.isTemplate()) {
	// embeddedValues = this.checkEmbeddedPropertyValues(displayMessages);
	// }
	// if (names && references && embeddedValues) {
	// // VALIDO
	// slotDef.setStatus(SlotDefSatus.VALID);
	// return true;
	// } else {
	// // NON VALIDO
	// slotDef.setStatus(SlotDefSatus.INVALID);
	// return false;
	// }
	// }

}
