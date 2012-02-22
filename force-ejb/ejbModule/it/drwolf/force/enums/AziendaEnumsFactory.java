package it.drwolf.force.enums;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("aziendaEnumsFactory")
@Scope(ScopeType.CONVERSATION)
public class AziendaEnumsFactory {

	@Factory("statoAziendaList")
	public List<StatoAzienda> getStatoAziendaList() {
		return Arrays.asList(StatoAzienda.values());
	}

	@Factory("tipologiaAbbonamentoList")
	public List<TipologiaAbbonamento> getTipologiaAbbonamentoList() {
		return Arrays.asList(TipologiaAbbonamento.values());
	}

}
