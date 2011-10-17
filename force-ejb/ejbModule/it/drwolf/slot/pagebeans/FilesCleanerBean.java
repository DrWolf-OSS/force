package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoAdminIdentity;
import it.drwolf.slot.entity.DocInst;
import it.drwolf.slot.pagebeans.support.FileDetails;

import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.EntityManager;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("filesCleanerBean")
@Scope(ScopeType.CONVERSATION)
public class FilesCleanerBean {

	@In
	private EntityManager entityManager;

	@In
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	private ArrayList<FileDetails> filesToBeDeleted = new ArrayList<FileDetails>();

	private String aspectId;

	public String deleteFiles() {
		Session session = this.alfrescoAdminIdentity.getSession();
		for (FileDetails fd : this.getFilesToBeDeleted()) {
			Document obj = (Document) session.getObject(fd.getObjectId());
			try {
				obj.deleteAllVersions();
				// dopo aver cancellato i file da alfresco devo pulire il db:
				ArrayList<DocInst> docInsts = (ArrayList<DocInst>) this.entityManager
						.createQuery("from DocInst where nodeRef = :nodeRef")
						.setParameter("nodeRef", fd.getObjectId())
						.getResultList();
				for (DocInst docInst : docInsts) {
					this.entityManager.remove(docInst);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return "OK";
	}

	public String getAspectId() {
		return this.aspectId;
	}

	public ArrayList<FileDetails> getFilesToBeDeleted() {
		return this.filesToBeDeleted;
	}

	public void locateFilesByAspect() {
		try {
			Session session = this.alfrescoAdminIdentity.getSession();
			String query = "SELECT D.cmis:name, D.cmis:objectId, D.cmis:createdBy FROM cmis:document AS D JOIN "
					+ this.getAspectId()
					+ " AS A ON D.cmis:objectId=A.cmis:objectId";
			ItemIterable<QueryResult> results = session.query(query, true);
			if (results.getTotalNumItems() != 0) {
				Iterator<QueryResult> iterator = results.iterator();
				while (iterator.hasNext()) {
					QueryResult result = iterator.next();
					String cmisObjectId = result
							.getPropertyValueById("cmis:objectId");
					String cmisObjectName = result
							.getPropertyValueById("cmis:name");
					String cmisCreatedBy = result
							.getPropertyValueById("cmis:createdBy");
					FileDetails fd = new FileDetails(cmisObjectId,
							cmisObjectName, cmisCreatedBy);
					this.filesToBeDeleted.add(fd);
				}
				System.out.println(this.filesToBeDeleted);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setAspectId(String aspectId) {
		this.aspectId = aspectId;
	}

	public void setFilesToBeDeleted(ArrayList<FileDetails> filesToBeDeleted) {
		this.filesToBeDeleted = filesToBeDeleted;
	}
}
