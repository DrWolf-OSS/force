package it.drwolf.slot.entitymanager;

import it.drwolf.slot.entity.DependentSlotDef;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.enums.DefStatus;
import it.drwolf.slot.enums.SlotDefSatus;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.ruleverifier.VerifierMessage;
import it.drwolf.slot.ruleverifier.VerifierMessageType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("slotDefValidator")
@Scope(ScopeType.CONVERSATION)
public class SlotDefValidator {

	private SlotDef slotDef;

	// @In(create = true)
	// private MessagesContainer messagesContainer;

	private ArrayList<VerifierMessage> messages;

	public SlotDefValidator() {
	}

	public SlotDefValidator(SlotDef slotDef) {
		super();
		this.slotDef = slotDef;
	}

	private boolean checkCollectionReferences(SlotDef slotDef) {
		boolean result = true;

		for (DocDefCollection collection : slotDef.getDocDefCollections()) {
			PropertyDef conditionalPropertyDef = collection
					.getConditionalPropertyDef();
			if (conditionalPropertyDef != null
					&& collection.getConditionalPropertyInst().getValue() == null) {
				result = false;
				//
				collection.setStatus(DefStatus.INVALID);
				//
				if (this.slotDef.equals(slotDef)) {
					// FacesMessages
					// .instance()
					// .add(Severity.ERROR,
					// "La richiesta \""
					// + collection.getName()
					// + "\" vincolata dall'informazione \""
					// + conditionalPropertyDef.getLabel()
					// + "\" non ha il valore del vincolo valorizzato");
					VerifierMessage message = new VerifierMessage(
							"La richiesta \""
									+ collection.getName()
									+ "\" vincolata dall'informazione \""
									+ conditionalPropertyDef.getLabel()
									+ "\" non ha il valore del vincolo valorizzato",
							VerifierMessageType.ERROR);
					this.getMessages().add(message);
				}
			} else {
				//
				// collection.setStatus(DefStatus.VALID);
				//
			}
		}
		if (!result && this.slotDef.equals(slotDef)) {
			// FacesMessages
			// .instance()
			// .add(Severity.ERROR,
			// "Non possono esserci richieste vincolate con il loro vincolo non valorizzato!");
			VerifierMessage message = new VerifierMessage(
					"Non possono esserci richieste vincolate con il loro vincolo non valorizzato!",
					VerifierMessageType.ERROR);
			this.getMessages().add(message);
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	private boolean checkEmbeddedPropertyValues(SlotDef slotDef) {
		boolean passed = true;
		for (EmbeddedProperty embeddedProperty : slotDef
				.getEmbeddedProperties()) {
			if ((!embeddedProperty.isMultiple() && (embeddedProperty.getValue() == null || embeddedProperty
					.getValue().equals("")))
					|| (embeddedProperty.isMultiple() && ((Set) embeddedProperty
							.getValue()).isEmpty())) {
				if (this.slotDef.equals(slotDef)) {
					// FacesMessages.instance().add(
					// Severity.ERROR,
					// "L'informazione \"" + embeddedProperty.getLabel()
					// + "\" non è stata valorizzata");
					VerifierMessage message = new VerifierMessage(
							"L'informazione \"" + embeddedProperty.getLabel()
									+ "\" non è stata valorizzata",
							VerifierMessageType.ERROR);
					this.getMessages().add(message);

				}
				passed = false;
				//
				embeddedProperty.setStatus(DefStatus.INVALID);
				//
			} else {
				//
				// embeddedProperty.setStatus(DefStatus.VALID);
				//
			}
		}
		if (!passed) {
			if (this.slotDef.equals(slotDef)) {
				// FacesMessages
				// .instance()
				// .add(Severity.ERROR,
				// "Non possono esserci informazioni sulla gara non valorizzate!");
				VerifierMessage message = new VerifierMessage(
						"Non possono esserci informazioni sulla gara non valorizzate!",
						VerifierMessageType.ERROR);
				this.getMessages().add(message);
			}
		}
		return passed;
	}

	private boolean checkNames(SlotDef slotDef) {
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
			this.flagInvalidProperties(allProperties);
			if (this.slotDef.equals(slotDef)) {
				// FacesMessages.instance().add(Severity.ERROR,
				// "Non possono esserci property con lo stesso nome");
				VerifierMessage message = new VerifierMessage(
						"Non possono esserci property con lo stesso nome",
						VerifierMessageType.ERROR);
				this.getMessages().add(message);
			}
		}

		differentNames.clear();
		for (DocDefCollection d : slotDef.getDocDefCollectionsAsList()) {
			differentNames.add(d.getName());
		}
		if (differentNames.size() < slotDef.getDocDefCollectionsAsList().size()) {
			result = false;
			this.flagInvalidCollections(slotDef.getDocDefCollectionsAsList());
			if (this.slotDef.equals(slotDef)) {
				// FacesMessages.instance().add(Severity.ERROR,
				// "Non possono esserci richieste con lo stesso nome");
				VerifierMessage message = new VerifierMessage(
						"Non possono esserci richieste con lo stesso nome",
						VerifierMessageType.ERROR);
				this.getMessages().add(message);
			}
		}

		return result;
	}

	private void flagInvalidCollections(List<DocDefCollection> collections) {
		Iterator<DocDefCollection> iterator = collections.iterator();
		while (iterator.hasNext()) {
			DocDefCollection collection = iterator.next();
			iterator.remove();
			for (DocDefCollection target : collections) {
				if (collection.getName().equals(target.getName())) {
					collection.setStatus(DefStatus.INVALID);
					target.setStatus(DefStatus.INVALID);
				}
			}
		}
	}

	private void flagInvalidProperties(List<DataDefinition> allProperties) {
		Iterator<DataDefinition> iterator = allProperties.iterator();
		while (iterator.hasNext()) {
			DataDefinition property = iterator.next();
			iterator.remove();
			for (DataDefinition target : allProperties) {
				if (property.getLabel().equals(target.getLabel())) {

					try {
						((PropertyDef) property).setStatus(DefStatus.INVALID);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						((EmbeddedProperty) property)
								.setStatus(DefStatus.INVALID);
					}

					try {
						((PropertyDef) target).setStatus(DefStatus.INVALID);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						((EmbeddedProperty) target)
								.setStatus(DefStatus.INVALID);
					}

				}
			}
		}
	}

	public ArrayList<VerifierMessage> getMessages() {
		return this.messages;
	}

	public SlotDef getSlotDef() {
		return this.slotDef;
	}

	private boolean ricorsiveValidation(SlotDef slotDef) {

		boolean result = true;
		if (slotDef.getDependentSlotDefs() != null
				&& !slotDef.getDependentSlotDefs().isEmpty()) {
			for (SlotDef dependentSlotDef : slotDef.getDependentSlotDefs()) {
				boolean dependentResult = this
						.ricorsiveValidation(dependentSlotDef);
				if (!dependentResult) {
					result = false;
					if (!this.slotDef.equals(dependentSlotDef)) {
						// FacesMessages
						// .instance()
						// .add(Severity.ERROR,
						// "La sottobusta \""
						// + dependentSlotDef.getName()
						// + "\" non è valida. Editarla singolarmente");
						VerifierMessage message = new VerifierMessage(
								"La sottobusta \""
										+ dependentSlotDef.getName()
										+ "\" non è valida. Editarla singolarmente",
								VerifierMessageType.ERROR);
						this.getMessages().add(message);
					}
				}
			}
		}
		boolean names = this.checkNames(slotDef);
		boolean references = this.checkCollectionReferences(slotDef);
		boolean embeddedValues = true;
		// Le embeddedProperty devono essere valorizzate nel caso in cui lo slot
		// sia:
		// - Uno SlotDef non modello
		// - Un DependentSlotDef modello
		// - Un DependentSlotDef associato ad uno SLotDef non modello (in
		// questo caso il Dependent non sarà flaggato come modello ma
		// il parent sì ed è quello che conta)
		if (!(slotDef instanceof DependentSlotDef)
				&& !slotDef.isTemplate()
				|| (slotDef instanceof DependentSlotDef && !((DependentSlotDef) slotDef)
						.getParentSlotDef().isTemplate())) {
			embeddedValues = this.checkEmbeddedPropertyValues(slotDef);
		}

		if (names && references && embeddedValues && result) {
			// VALIDO
			slotDef.setStatus(SlotDefSatus.VALID);
			return true;
		} else {
			// NON VALIDO
			slotDef.setStatus(SlotDefSatus.INVALID);
			return false;
		}
	}

	public void setMessages(ArrayList<VerifierMessage> messages) {
		this.messages = messages;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	public void validate() {
		if (this.slotDef instanceof DependentSlotDef) {
			boolean valid = this
					.ricorsiveValidation(((DependentSlotDef) this.slotDef)
							.getParentSlotDef());
			if (!valid && this.slotDef.getStatus().equals(SlotDefSatus.VALID)) {
				// FacesMessages
				// .instance()
				// .add(Severity.ERROR,
				// "La busta contenitrice \""
				// + ((DependentSlotDef) this.slotDef)
				// .getParentSlotDef().getName()
				// +
				// "\" non è valida. Controllare la busta principale o altre sue sottobuste");
				VerifierMessage message = new VerifierMessage(
						"La busta contenitrice \""
								+ ((DependentSlotDef) this.slotDef)
										.getParentSlotDef().getName()
								+ "\" non è valida. Controllare la busta principale o altre sue sottobuste",
						VerifierMessageType.ERROR);
				this.getMessages().add(message);
			}
			// return valid;
		} else {
			// return
			this.ricorsiveValidation(this.slotDef);
		}
	}

}
