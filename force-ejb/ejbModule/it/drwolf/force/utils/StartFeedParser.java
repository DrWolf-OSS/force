package it.drwolf.force.utils;

import it.drwolf.force.interfaces.GaraFeedParserIF;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class StartFeedParser implements GaraFeedParserIF {

	private HashMap<String, String> feedElements = new HashMap<String, String>();
	private ArrayList<String> soa = new ArrayList<String>();
	private ArrayList<String> cm = new ArrayList<String>();

	public List<String> getCategorie() {
		return this.cm;
	}

	public Date getDataFine() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		if (this.feedElements.containsKey("Fine gara:")) {
			try {
				return sdf.parse(this.feedElements.get("Fine gara:"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (this.feedElements
				.containsKey("Scadenza fase di manifestazione d'interesse:")) {
			try {
				return sdf.parse(this.feedElements
						.get("Scadenza fase di manifestazione d'interesse:"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Date getDataInizio() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		if (this.feedElements.containsKey("Inizio gara:")) {
			try {
				return sdf.parse(this.feedElements.get("Inizio gara:"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (this.feedElements
				.containsKey("Inizio fase di manifestazione d'interesse:")) {
			try {
				return sdf.parse(this.feedElements
						.get("Inizio fase di manifestazione d'interesse:"));
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	public BigDecimal getImporto() {
		for (String key : this.feedElements.keySet()) {
			if (key.contains("importo") || key.contains("Importo")) {
				try {
					String[] t = this.feedElements.get(key).split(" ");
					return new BigDecimal(t[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public ArrayList<String> getSoa() {
		return this.soa;
	}

	public boolean haveCm() {
		if (this.cm.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean haveSoa() {
		if (this.soa.size() > 0) {
			return true;
		}
		return false;
	}

	public boolean isAperta() {
		if (this.feedElements.containsKey("Procedura:")) {
			if (this.feedElements.get("Procedura:").equals("Aperta")) {
				return true;
			}
		}
		return false;
	}

	public boolean isNegoziata() {
		if (this.feedElements.containsKey("Procedura:")) {
			if (this.feedElements.get("Procedura:").equals("Negoziata")) {
				return true;
			}
		}
		return false;
	}

	public boolean isOffLine() {
		if (this.feedElements.containsKey("Svolgimento della gara:")) {
			if (this.feedElements.get("Svolgimento della gara:").equals(
					"Tradizionale (off line)")) {
				return true;
			}
		}
		return false;
	}

	public boolean isOnLine() {
		if (this.feedElements.containsKey("Svolgimento della gara:")) {
			if (this.feedElements.get("Svolgimento della gara:").equals(
					"Telematica (on line)")) {
				return true;
			}
		}
		return false;
	}

	public boolean isValid() {
		if (this.feedElements.isEmpty()) {
			return false;
		}
		return true;
	}

	public void parse(HtmlPage page) {
		this.feedElements = new HashMap<String, String>();
		this.soa = new ArrayList<String>();
		this.cm = new ArrayList<String>();
		Pattern pCM = Pattern.compile("^[A-Z0-9\\-\\.\\s,]+$");
		Pattern pSOA = Pattern.compile("^O[GS]\\s\\d");
		Pattern commento = Pattern.compile("^\\d+$");
		try {
			List<HtmlTableRow> rows = (List<HtmlTableRow>) page
					.getByXPath("//tr");
			for (HtmlTableRow row : rows) {
				if (row.getCells().size() > 1) {
					if (row.getCell(0).asText().endsWith(":")) {
						// se finisce con i :
						this.feedElements.put(row.getCell(0).asText().trim(),
								row.getCell(1).asText().trim());
					} else {
						// provo a vedere se è una SOA o CM
						Matcher mSOA = pSOA.matcher(row.getCell(0).asText());
						Matcher mCM = pCM.matcher(row.getCell(0).asText());
						Matcher mCommento = commento.matcher(row.getCell(0)
								.asText());
						if (!mCommento.find()) {
							if (mSOA.find()) {
								// Ho individuato una categoria merceologica
								this.soa.add(row.getCell(0).asText().trim());
							} else if (mCM.find()) {
								this.cm.add(row.getCell(0).asText().trim());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
