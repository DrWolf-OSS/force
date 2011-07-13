package it.drwolf.slot.digsig;

import it.drwolf.slot.alfresco.AlfrescoAdminIdentity;

import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("alfrescoNodeCertsProvider")
@Scope(ScopeType.CONVERSATION)
public class AlfrescoNodeCertsProvider extends CertsProviderNotifier implements
		ICertsProvider {

	private String nodeRef;

	@In(create = true)
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	public void dispose() {
		nodeRef = null;
	}

	/**
	 * fornisce l'infrastruttura di base per ottenere lo stream dato un noderef
	 * 
	 */
	public InputStream getResource() {
		Document document = (Document) alfrescoAdminIdentity.getSession()
				.getObject(nodeRef);
		return document.getContentStream().getStream();
	}

	/**
	 * nodeRef del file dal quale si possono recuperare i certificati che sar�
	 * del tipo LISTACER.zip.p7m. Questo campo viene impostato tramite il
	 * parametro immesso nell'azione di estrazione dati del certificato
	 * 
	 * @param nodeRef
	 */
	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
		notifyResourceChange();

	}

	public String getNodeRef() {
		return nodeRef;
	}

}
