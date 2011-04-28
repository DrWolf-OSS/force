package it.drwolf.slot.alfresco;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.ChangeEvent;
import org.apache.chemistry.opencmis.client.api.ChangeEvents;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.spi.CmisBinding;

public class SynchSession implements Session {

	public SynchSession(Session session) {
		super();
		this.session = session;
	}

	private Session session;

	public Acl applyAcl(ObjectId arg0, List<Ace> arg1, List<Ace> arg2,
			AclPropagation arg3) {
		return session.applyAcl(arg0, arg1, arg2, arg3);
	}

	public void applyPolicy(ObjectId arg0, ObjectId... arg1) {
		session.applyPolicy(arg0, arg1);
	}

	public void clear() {
		session.clear();
	}

	public synchronized ObjectId createDocument(Map<String, ?> arg0,
			ObjectId arg1, ContentStream arg2, VersioningState arg3,
			List<Policy> arg4, List<Ace> arg5, List<Ace> arg6) {
		return session.createDocument(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	public synchronized ObjectId createDocument(Map<String, ?> arg0,
			ObjectId arg1, ContentStream arg2, VersioningState arg3) {
		return session.createDocument(arg0, arg1, arg2, arg3);
	}

	public synchronized ObjectId createDocumentFromSource(ObjectId arg0,
			Map<String, ?> arg1, ObjectId arg2, VersioningState arg3,
			List<Policy> arg4, List<Ace> arg5, List<Ace> arg6) {
		return session.createDocumentFromSource(arg0, arg1, arg2, arg3, arg4,
				arg5, arg6);
	}

	public synchronized ObjectId createDocumentFromSource(ObjectId arg0,
			Map<String, ?> arg1, ObjectId arg2, VersioningState arg3) {
		return session.createDocumentFromSource(arg0, arg1, arg2, arg3);
	}

	public synchronized ObjectId createFolder(Map<String, ?> arg0,
			ObjectId arg1, List<Policy> arg2, List<Ace> arg3, List<Ace> arg4) {
		return session.createFolder(arg0, arg1, arg2, arg3, arg4);
	}

	public synchronized ObjectId createFolder(Map<String, ?> arg0, ObjectId arg1) {
		return session.createFolder(arg0, arg1);
	}

	public synchronized ObjectId createObjectId(String arg0) {
		return session.createObjectId(arg0);
	}

	public synchronized OperationContext createOperationContext() {
		return session.createOperationContext();
	}

	public synchronized OperationContext createOperationContext(
			Set<String> arg0, boolean arg1, boolean arg2, boolean arg3,
			IncludeRelationships arg4, Set<String> arg5, boolean arg6,
			String arg7, boolean arg8, int arg9) {
		return session.createOperationContext(arg0, arg1, arg2, arg3, arg4,
				arg5, arg6, arg7, arg8, arg9);
	}

	public ObjectId createPolicy(Map<String, ?> arg0, ObjectId arg1,
			List<Policy> arg2, List<Ace> arg3, List<Ace> arg4) {
		return session.createPolicy(arg0, arg1, arg2, arg3, arg4);
	}

	public ObjectId createPolicy(Map<String, ?> arg0, ObjectId arg1) {
		return session.createPolicy(arg0, arg1);
	}

	public ObjectId createRelationship(Map<String, ?> arg0, List<Policy> arg1,
			List<Ace> arg2, List<Ace> arg3) {
		return session.createRelationship(arg0, arg1, arg2, arg3);
	}

	public ObjectId createRelationship(Map<String, ?> arg0) {
		return session.createRelationship(arg0);
	}

	public Acl getAcl(ObjectId arg0, boolean arg1) {
		return session.getAcl(arg0, arg1);
	}

	public CmisBinding getBinding() {
		return session.getBinding();
	}

	public ItemIterable<Document> getCheckedOutDocs() {
		return session.getCheckedOutDocs();
	}

	public ItemIterable<Document> getCheckedOutDocs(OperationContext arg0) {
		return session.getCheckedOutDocs(arg0);
	}

	public ChangeEvents getContentChanges(String arg0, boolean arg1, long arg2,
			OperationContext arg3) {
		return session.getContentChanges(arg0, arg1, arg2, arg3);
	}

	public ChangeEvents getContentChanges(String arg0, boolean arg1, long arg2) {
		return session.getContentChanges(arg0, arg1, arg2);
	}

	public OperationContext getDefaultContext() {
		return session.getDefaultContext();
	}

	public Locale getLocale() {
		return session.getLocale();
	}

	public CmisObject getObject(ObjectId arg0, OperationContext arg1) {
		return session.getObject(arg0, arg1);
	}

	public CmisObject getObject(ObjectId arg0) {
		return session.getObject(arg0);
	}

	public CmisObject getObject(String arg0, OperationContext arg1) {
		return session.getObject(arg0, arg1);
	}

	public CmisObject getObject(String arg0) {
		return session.getObject(arg0);
	}

	public CmisObject getObjectByPath(String arg0, OperationContext arg1) {
		return session.getObjectByPath(arg0, arg1);
	}

	public CmisObject getObjectByPath(String arg0) {
		return session.getObjectByPath(arg0);
	}

	public ObjectFactory getObjectFactory() {
		return session.getObjectFactory();
	}

	public ItemIterable<Relationship> getRelationships(ObjectId arg0,
			boolean arg1, RelationshipDirection arg2, ObjectType arg3,
			OperationContext arg4) {
		return session.getRelationships(arg0, arg1, arg2, arg3, arg4);
	}

	public RepositoryInfo getRepositoryInfo() {
		return session.getRepositoryInfo();
	}

	public Folder getRootFolder() {
		return session.getRootFolder();
	}

	public Folder getRootFolder(OperationContext arg0) {
		return session.getRootFolder(arg0);
	}

	public ItemIterable<ObjectType> getTypeChildren(String arg0, boolean arg1) {
		return session.getTypeChildren(arg0, arg1);
	}

	public ObjectType getTypeDefinition(String arg0) {
		return session.getTypeDefinition(arg0);
	}

	public List<Tree<ObjectType>> getTypeDescendants(String arg0, int arg1,
			boolean arg2) {
		return session.getTypeDescendants(arg0, arg1, arg2);
	}

	public ItemIterable<QueryResult> query(String arg0, boolean arg1,
			OperationContext arg2) {
		return session.query(arg0, arg1, arg2);
	}

	public ItemIterable<QueryResult> query(String arg0, boolean arg1) {
		return session.query(arg0, arg1);
	}

	public void removePolicy(ObjectId arg0, ObjectId... arg1) {
		session.removePolicy(arg0, arg1);
	}

	public void setDefaultContext(OperationContext arg0) {
		session.setDefaultContext(arg0);
	}

	public void cancel() {
		// TODO Auto-generated method stub

	}

	public ItemIterable<ChangeEvent> getContentChanges(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save() {
		// TODO Auto-generated method stub

	}

	// public void cancel() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// public ItemIterable<ChangeEvent> getContentChanges(String arg0) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public void save() {
	// // TODO Auto-generated method stub
	//
	// }

}
