package it.drwolf.slot.alfresco;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Name("alfrescoUserIdentity")
@Scope(ScopeType.SESSION)
public class AlfrescoUserIdentity extends AlfrescoIdentity {

}
