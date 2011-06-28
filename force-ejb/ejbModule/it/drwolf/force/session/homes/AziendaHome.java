package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.enums.PosizioneCNA;
import it.drwolf.force.enums.StatoAzienda;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.Identity;

@Name("aziendaHome")
public class AziendaHome extends EntityHome<Azienda> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214123840587373460L;

	@In
	Identity identity;

	@In(create = true)
	AlfrescoInfo alfrescoInfo;

	@In(create = true)
	AlfrescoWrapper alfrescoWrapper;

	@In
	EntityManager entityManager;

	@Override
	protected Azienda createInstance() {
		Azienda azienda = new Azienda();
		return azienda;
	}

	public Long getAziendaId() {
		return (Long) this.getId();
	}

	public Azienda getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public List<PosizioneCNA> getPosizioneCNA() {
		return Arrays.asList(PosizioneCNA.values());
	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
	}

	@Override
	public String persist() {
		try {
			this.getInstance().setStato(StatoAzienda.NUOVA.toString());
			// persisto l'entity azienda
			super.persist();
			// creazione del nome del gruppo
			String groupName = this.getInstance().getRagioneSociale()
					.replaceAll("\\s", "_");
			groupName = groupName + "_" + this.getInstance().getId();

			AlfrescoWebScriptClient awsc = new AlfrescoWebScriptClient(
					this.alfrescoInfo.getAdminUser(),
					this.alfrescoInfo.getAdminPwd(),
					this.alfrescoInfo.getRepositoryUri());

			String res = awsc.addGroupOrUser("aziende", groupName, true);

			// creo la cartella groupName sotto il progetto Force
			AlfrescoFolder alfrescoGroupFolder = this.alfrescoWrapper
					.findOrCreateFolder(
							this.alfrescoWrapper.getMainProjectFolder(),
							groupName);

			// creo l'utente con l'email del referente
			HashMap<String, String> args = new HashMap<String, String>();
			args.put("userName", this.getInstance().getEmailReferente());
			args.put("firstName", this.getInstance().getNome());
			args.put("lastName", this.getInstance().getCognome());
			args.put("email", this.getInstance().getEmailReferente());
			// forse dovrei settare la password dato che di default è a
			// "password"?
			awsc.addPerson(args);

			// Aggiungo l'utente al gruppo creato in precedenza
			awsc.addGroupOrUser(groupName, this.getInstance()
					.getEmailReferente(), false);

			// Aggiungo i provilegi di collaboratore all'utente creato sulla
			// cartella
			// groupName
			this.alfrescoWrapper.applyACL(alfrescoGroupFolder, "GROUP_"
					+ groupName);

			// se l'azienda è amministrata gli utenti del gruppo "CNA" devono i
			// permessi sulla cartella
			if (this.getInstance().getPosizioneCNA() == PosizioneCNA.AMMINISTRATA
					.toString()) {
				this.alfrescoWrapper.applyACL(alfrescoGroupFolder, "GROUP_CNA");

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "KO";
		}

		return "OK";
	}

	public void setAziendaId(Long id) {
		this.setId(id);
	}

	public void wire() {
		this.getInstance();
	}
}
