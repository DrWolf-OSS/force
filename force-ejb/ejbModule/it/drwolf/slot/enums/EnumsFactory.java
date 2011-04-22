package it.drwolf.slot.enums;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("enumsFactory")
@Scope(ScopeType.CONVERSATION)
public class EnumsFactory {

	@Factory("aspects")
	public List<Aspect> getAspects() {
		return Arrays.asList(Aspect.values());
	}

}
