package it.drwolf.force.utils;

import it.drwolf.force.interfaces.GaraFeedParserIF;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class StartFeedParser implements GaraFeedParserIF {

	HtmlPage page;

	private HashMap<String, String> feedElements = new HashMap<String, String>();
	private ArrayList<String> soa = new ArrayList<String>();
	private ArrayList<String> cm = new ArrayList<String>();

	public List<String> getCategorie() {
		Pattern pCM = Pattern.compile("^[A-Z0-9\\- ]+$");
		Pattern pSOA = Pattern.compile("^O[GS]\\s\\d");
		ArrayList<String> categorie = new ArrayList<String>();
		List<HtmlTableRow> rows = (List<HtmlTableRow>) this.page
				.getByXPath("//tr");
		for (HtmlTableRow row : rows) {
			for (HtmlTableCell cell : row.getCells()) {
				String[] res = cell.asText().split(":");
				if (res.length == 2) {
					Matcher mCM = pCM.matcher(res[1]);
					Matcher mSOA = pSOA.matcher(res[0]);
					if (mCM.find()) {
						// Ho individuato una categoria merceologica
						categorie.add(res[1].trim());
					} else if (mSOA.find()) {
						categorie.add(res[0].trim());
					}
				}
			}

		}
		return categorie;
	}

	public ArrayList<String> getCM() {
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

	public ArrayList<String> getSOA() {
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

	public void parse(String url) {
		WebClient wc = new WebClient();
		Pattern pCM = Pattern.compile("^[A-Z0-9\\-\\.\\s,]+$");
		Pattern pSOA = Pattern.compile("^O[GS]\\s\\d");
		Pattern commento = Pattern.compile("^\\d+$");
		try {
			this.page = wc.getPage(url);
			List<HtmlTableRow> rows = (List<HtmlTableRow>) this.page
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
			System.out.println(this.feedElements);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
