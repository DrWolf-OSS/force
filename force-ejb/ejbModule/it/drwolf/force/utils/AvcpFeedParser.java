package it.drwolf.force.utils;

import it.drwolf.force.interfaces.GaraFeedParserIF;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class AvcpFeedParser implements GaraFeedParserIF {

	private HashMap<String, String> feedElements = new HashMap<String, String>();
	private ArrayList<String> soa = new ArrayList<String>();
	private ArrayList<String> cpv = new ArrayList<String>();

	private BigDecimal importo;

	private static final Map<String, String> codificaSOA = new HashMap<String, String>();
	static {
		AvcpFeedParser.codificaSOA.put("OG1", "OG 01");
		AvcpFeedParser.codificaSOA.put("OG2", "OG 02");
		AvcpFeedParser.codificaSOA.put("OG3", "OG 03");
		AvcpFeedParser.codificaSOA.put("OG4", "OG 04");
		AvcpFeedParser.codificaSOA.put("OG5", "OG 05");
		AvcpFeedParser.codificaSOA.put("OG6", "OG 06");
		AvcpFeedParser.codificaSOA.put("OG7", "OG 07");
		AvcpFeedParser.codificaSOA.put("OG8", "OG 08");
		AvcpFeedParser.codificaSOA.put("OG9", "OG 09");
		AvcpFeedParser.codificaSOA.put("OG10", "OG 10");
		AvcpFeedParser.codificaSOA.put("OG11", "OG 11");
		AvcpFeedParser.codificaSOA.put("OG12", "OG 12");
		AvcpFeedParser.codificaSOA.put("OG13", "OG 13");
		AvcpFeedParser.codificaSOA.put("OS1", "OS 01");
		AvcpFeedParser.codificaSOA.put("OS2-A", "OS 02-A");
		AvcpFeedParser.codificaSOA.put("OS2-B", "OS 02-B");
		AvcpFeedParser.codificaSOA.put("OS3", "OS 03");
		AvcpFeedParser.codificaSOA.put("OS4", "OS 04");
		AvcpFeedParser.codificaSOA.put("OS5", "OS 05");
		AvcpFeedParser.codificaSOA.put("OS6", "OS 06");
		AvcpFeedParser.codificaSOA.put("OS7", "OS 07");
		AvcpFeedParser.codificaSOA.put("OS8", "OS 08");
		AvcpFeedParser.codificaSOA.put("OS9", "OS 09");
		AvcpFeedParser.codificaSOA.put("OS10", "OS 10");
		AvcpFeedParser.codificaSOA.put("OS11", "OS 11");
		AvcpFeedParser.codificaSOA.put("OS12-A", "OS 12-A");
		AvcpFeedParser.codificaSOA.put("OS12-B", "OS 12-B");
		AvcpFeedParser.codificaSOA.put("OS13", "OS 13");
		AvcpFeedParser.codificaSOA.put("OS14", "OS 14");
		AvcpFeedParser.codificaSOA.put("OS15", "OS 15");
		AvcpFeedParser.codificaSOA.put("OS16", "OS 16");
		AvcpFeedParser.codificaSOA.put("OS17", "OS 17");
		AvcpFeedParser.codificaSOA.put("OS18-A", "OS 18-A");
		AvcpFeedParser.codificaSOA.put("OS19-A", "OS 19-A");
		AvcpFeedParser.codificaSOA.put("OS19", "OS 19");
		AvcpFeedParser.codificaSOA.put("OS20-A", "OS 20-A");
		AvcpFeedParser.codificaSOA.put("OS20-B", "OS 20B");
		AvcpFeedParser.codificaSOA.put("OS21", "OS 21");
		AvcpFeedParser.codificaSOA.put("OS22", "OS 22");
		AvcpFeedParser.codificaSOA.put("OS23", "OS 23");
		AvcpFeedParser.codificaSOA.put("OS24", "OS 24");
		AvcpFeedParser.codificaSOA.put("OS25", "OS 25");
		AvcpFeedParser.codificaSOA.put("OS26", "OS 26");
		AvcpFeedParser.codificaSOA.put("OS27", "OS 27");
		AvcpFeedParser.codificaSOA.put("OS28", "OS 28");
		AvcpFeedParser.codificaSOA.put("OS29", "OS 29");
		AvcpFeedParser.codificaSOA.put("OS30", "OS 30");
		AvcpFeedParser.codificaSOA.put("OS31", "OS 31");
		AvcpFeedParser.codificaSOA.put("OS32", "OS 32");
		AvcpFeedParser.codificaSOA.put("OS33", "OS 33");
		AvcpFeedParser.codificaSOA.put("OS34", "OS 34");
		AvcpFeedParser.codificaSOA.put("OS35", "OS 35");
	}

	private void extractCPV(String stringCPV) {
		Pattern pCPV = Pattern.compile("^(\\d+-\\d)\\s-\\s.*$");
		Matcher mCPV = pCPV.matcher(stringCPV);
		if (mCPV.find()) {
			// metto i codici cpv come se fossero categorie merceologiche perchè
			// in questa classe non ho l'entity manager
			// La conversione da cpv a cm la faccio nell'hearthbeat
			String codice = mCPV.group(1);
			this.cpv.add(codice);
		}
	}

	private String extractImporto(String importo) {
		return importo.replaceAll("€", "").replaceAll(" ", "")
				.replaceAll(",", "");
	}

	private void extractSoa(String stringaSOA) {
		Pattern pSOA = Pattern.compile("(O[GS]\\d)");
		Matcher mSOA = pSOA.matcher(stringaSOA);
		if (mSOA.find()) {
			for (int i = 1; i <= mSOA.groupCount(); i++) {
				String soa = mSOA.group(i);
				if (AvcpFeedParser.codificaSOA.containsKey(soa)) {
					this.soa.add(AvcpFeedParser.codificaSOA.get(soa));
				} else {
					System.out
							.println("Non ho ideficato il seguente codice SOA"
									+ soa);
				}
			}
		}

	}

	public List<String> getCategorie() {
		return this.cpv;
	}

	public Date getDataFine() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (this.feedElements.containsKey("Scadenza")) {
			try {
				return sdf.parse(this.feedElements.get("Scadenza"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Date getDataInizio() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (this.feedElements.containsKey("Data pubblicazione GURI")) {
			try {
				return sdf.parse(this.feedElements
						.get("Data pubblicazione GURI"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public BigDecimal getImporto() {
		return this.importo;
	}

	public String getOggetto() {
		if (this.feedElements.containsKey("Oggetto")) {
			return this.feedElements.get("Oggetto");
		}
		return null;
	}

	public List<String> getSoa() {
		return this.soa;
	}

	public boolean haveCm() {
		if (this.cpv.size() > 0) {
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

	public boolean isValid() {
		if (this.feedElements.isEmpty()) {
			return false;
		}
		return true;
	}

	public void parse(HtmlPage page) {
		this.feedElements = new HashMap<String, String>();
		this.soa = new ArrayList<String>();
		this.cpv = new ArrayList<String>();
		// prima prelevo le informazioni dalla pagina di dettaglio
		HtmlTable table = page.getHtmlElementById("detail");
		for (HtmlTableRow row : table.getRows()) {
			if (row.getCells().size() > 1
					&& !row.getCell(1).asText().equals("")) {
				this.feedElements.put(row.getCell(0).asText().trim(), row
						.getCell(1).asText().trim());
			}
		}
		HtmlTable lotti = page.getHtmlElementById("listaLotti");
		BigDecimal importoTotale = new BigDecimal(0);
		for (HtmlTableRow row : lotti.getRows()) {
			if (row.getCells().size() > 1) {
				List<HtmlElement> a = row.getCell(0).getHtmlElementsByTagName(
						"a");
				if (a.size() == 1) {
					HtmlAnchor link = (HtmlAnchor) a.get(0);
					try {
						HtmlPage dettaglioLotti = link.click();
						HtmlTable t = dettaglioLotti
								.getHtmlElementById("detail");
						for (HtmlTableRow r : t.getRows()) {
							if (r.getCells().size() > 1
									&& !r.getCell(1).asText().equals("")) {
								if (r.getCell(0).asText()
										.equals("Categoria di qualificazione")) {
									this.extractSoa(r.getCell(1).asText());
								}
								if (r.getCell(0).asText().equals("CPV")) {
									this.extractCPV(r.getCell(1).asText());
								}
								if (r.getCell(0).asText()
										.contains("Importo a base")) {
									String importoStr = this.extractImporto(r
											.getCell(1).asText());
									if (importoStr != null) {
										NumberFormat fmt_IT = NumberFormat
												.getNumberInstance(Locale.ITALIAN);
										BigDecimal i = new BigDecimal(
												importoStr);
										importoTotale = importoTotale.add(i);

									}
								}
							} else {
								this.feedElements.put(r.getCell(0).asText()
										.trim(), r.getCell(1).asText().trim());
							}

						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		this.importo = importoTotale;
		System.out.println(this.feedElements.toString().replaceAll(",", "\n"));

	}
}
