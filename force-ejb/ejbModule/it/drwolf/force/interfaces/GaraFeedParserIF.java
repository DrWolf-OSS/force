package it.drwolf.force.interfaces;

import java.util.Date;
import java.util.List;

public interface GaraFeedParserIF {

	public List<String> getCategorie();

	public Date getDataFine();

	public Date getDataInizio();

	public void parse(String url);
}
