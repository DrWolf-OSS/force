package it.drwolf.force.interfaces;

import java.util.List;

public interface GaraFeedParserIF {

	public List<String> getCategorie();

	public void parse(String url);
}
