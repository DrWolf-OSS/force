package it.drwolf.force.interfaces;

import java.util.Date;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface GaraFeedParserIF {

	public List<String> getCategorie();

	public Date getDataFine();

	public Date getDataInizio();

	public List<String> getSoa();

	public boolean haveCm();

	public boolean haveSoa();

	public boolean isValid();

	public void parse(HtmlPage page);
}
