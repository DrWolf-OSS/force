package it.drwolf.force.session;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("adminUserSession")
@Scope(ScopeType.SESSION)
@AutoCreate
public class AdminUserSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8437341415271744919L;

	public void init() {
		// per non faccio niente
		// poi vediamo
	}
}
